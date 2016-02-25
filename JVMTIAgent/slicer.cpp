#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include <string.h>
#include "jvmti.h"
#include "jni.h"
#include "classfile_constants.h"
#define _STRING(s) #s
#define STRING(s) _STRING(s)
typedef struct {
	/* JVMTI Environment */
	jvmtiEnv *jvmti;
	JNIEnv * jni;
	jboolean vm_is_started;
	jboolean vmDead;

	/* Data access Lock */
	jrawMonitorID lock;
	JavaVM* jvm;
} GlobalAgentData;


static jvmtiEnv *jvmti = NULL;
static jvmtiCapabilities capa;
static GlobalAgentData *gdata;

/* Send message to stdout or whatever the data output location is */
void stdout_message(const char * format, ...) {
	va_list ap;

	va_start(ap, format);
	(void) vfprintf(stdout, format, ap);
	va_end(ap);
}
/* Send message to stderr or whatever the error output location is and exit  */
void fatal_error(const char * format, ...) {
	va_list ap;

	va_start(ap, format);
	(void) vfprintf(stderr, format, ap);
	(void) fflush(stderr);
	va_end(ap);
	exit(3);
}

/* Every JVMTI interface returns an error code, which should be checked
 *   to avoid any cascading errors down the line.
 *   The interface GetErrorName() returns the actual enumeration constant
 *   name, making the error messages much easier to understand.
 */
static void check_jvmti_error(jvmtiEnv *jvmti, jvmtiError errnum,
		const char *str) {
	if (errnum != JVMTI_ERROR_NONE) {
		char *errnum_str;

		errnum_str = NULL;
		(void) jvmti->GetErrorName(errnum, &errnum_str);

		printf("ERROR: JVMTI: %d(%s): %s\n", errnum,
				(errnum_str == NULL ? "Unknown" : errnum_str),
				(str == NULL ? "" : str));
	}
}

/* Enter a critical section by doing a JVMTI Raw Monitor Enter */
static void enter_critical_section(jvmtiEnv *jvmti) {
	jvmtiError error;

	error = jvmti->RawMonitorEnter(gdata->lock);
	check_jvmti_error(jvmti, error, "Cannot enter with raw monitor");
}

/* Exit a critical section by doing a JVMTI Raw Monitor Exit */
static void exit_critical_section(jvmtiEnv *jvmti) {
	jvmtiError error;

	error = jvmti->RawMonitorExit(gdata->lock);
	check_jvmti_error(jvmti, error, "Cannot exit with raw monitor");
}

void describe(jvmtiError err) {
	jvmtiError err0;
	char *descr;
	err0 = jvmti->GetErrorName(err, &descr);
	if (err0 == JVMTI_ERROR_NONE) {
		printf(descr);
	} else {
		printf("error [%d]", err);
	}
}

/*
 * Implementation of _setTag JNI function.
 */
JNIEXPORT static void JNICALL setTag(JNIEnv *env, jclass klass,
		jobject o, jobject expr) {
	if (gdata->vmDead) {
		return;
	}
	if(!o)
	{
		return;
	}
	jvmtiError error;
	jlong tag;
	if (expr) {
		//Set the tag, make a new global reference to it
		error = gdata->jvmti->SetTag(o, (jlong) (ptrdiff_t) (void*) env->NewGlobalRef(expr));
	} else {
		error = gdata->jvmti->SetTag(o, 0);
	}
	if(error == JVMTI_ERROR_WRONG_PHASE)
	return;
	check_jvmti_error(gdata->jvmti, error, "Cannot set object tag");
}
/*
 * Implementation of _getTag JNI function
 */
JNIEXPORT static jobject JNICALL getTag(JNIEnv *env, jclass klass,
		jobject o) {
	if (gdata->vmDead) {
		return NULL;
	}
	jvmtiError error;
	jlong tag;
	error = gdata->jvmti->GetTag(o, &tag);
	if(error == JVMTI_ERROR_WRONG_PHASE)
	return NULL;
	check_jvmti_error(gdata->jvmti, error, "Cannot get object tag");
	if(tag)
	{
		return (jobject) (ptrdiff_t) tag;
	}
	return NULL;
}
/* Callback for JVMTI_EVENT_VM_START */
static void JNICALL
cbVMStart(jvmtiEnv *jvmti, JNIEnv *env) {

	enter_critical_section(jvmti);
	{
		jclass klass;
		jfieldID field;
		jint rc;
		jvmtiJlocationFormat fmt;

		/* Java Native Methods for class */
		static JNINativeMethod registry[2] = { { "_getTag", "(Ljava/lang/Object;)Lde/unisb/cs/st/javaslicer/tracer/mt/ObjectAccessLog;",
				(void*) &getTag }, { "_setTag", "(Ljava/lang/Object;Lde/unisb/cs/st/javaslicer/tracer/mt/ObjectAccessLog;)V",
						(void*) &setTag } };
		/* Register Natives for class whose methods we use */
		klass = env->FindClass(
				"de/unisb/cs/st/javaslicer/tracer/mt/ObjectAccessLog");
		if (klass == NULL) {
			fatal_error(
					"ERROR: JNI: Cannot find de/unisb/cs/st/javaslicer/tracer/mt/ObjectAccessLog with FindClass\n");
		}
		rc = env->RegisterNatives(klass, registry, 2);
		if (rc != 0) {
			fatal_error(
					"ERROR: JNI: Cannot register natives for class de/unisb/cs/st/javaslicer/tracer/mt/ObjectAccessLog\n");
		}
		/* Engage calls. */
		field = env->GetStaticFieldID(klass, "engaged", "I");
		if (field == NULL) {
			fatal_error("ERROR: JNI: Cannot get field from %s\n",
					STRING(HEAP_TRACKER_class));
		}
		env->SetStaticIntField(klass, field, 1);

		/* Indicate VM has started */
		gdata->vm_is_started = JNI_TRUE;
		jvmti->GetJLocationFormat(&fmt);
		if (fmt != 1) {
			fatal_error(
					"Error: For some reason, your JVM is not providing bytecode indices for stack traces");
		}
	}
	exit_critical_section(jvmti);
}
// VM Death callback
static void JNICALL callbackVMDeath(jvmtiEnv *jvmti_env, JNIEnv* jni_env) {
	gdata->vmDead = JNI_TRUE;
}

static void JNICALL callbackVMInit(jvmtiEnv * jvmti, JNIEnv * env,
		jthread thread) {
	jvmtiError err;
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options,
		void *reserved) {
	static GlobalAgentData data;
	jvmtiError error;
	jint res;
	jvmtiEventCallbacks callbacks;

	/* Setup initial global agent data area
	 *   Use of static/extern data should be handled carefully here.
	 *   We need to make sure that we are able to cleanup after ourselves
	 *     so anything allocated in this library needs to be freed in
	 *     the Agent_OnUnload() function.
	 */
	(void) memset((void*) &data, 0, sizeof(data));
	gdata = &data;

	/*  We need to first get the jvmtiEnv* or JVMTI environment */

	gdata->jvm = jvm;
	res = jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_0);

	if (res != JNI_OK || jvmti == NULL) {
		/* This means that the VM was unable to obtain this version of the
		 *   JVMTI interface, this is a fatal error.
		 */
		printf("ERROR: Unable to access JVMTI Version 1 (0x%x),"
				" is your J2SE a 1.5 or newer version?"
				" JNIEnv's GetEnv() returned %d\n", JVMTI_VERSION_1, res);

	}

	/* Here we save the jvmtiEnv* for Agent_OnUnload(). */
	gdata->jvmti = jvmti;
	(void) memset(&capa, 0, sizeof(jvmtiCapabilities));
	capa.can_tag_objects = 1;

	error = jvmti->AddCapabilities(&capa);
	check_jvmti_error(jvmti, error,
			"Unable to get necessary JVMTI capabilities.");

	(void) memset(&callbacks, 0, sizeof(callbacks));
	callbacks.VMInit = &callbackVMInit; /* JVMTI_EVENT_VM_INIT */
	callbacks.VMDeath = &callbackVMDeath; /* JVMTI_EVENT_VM_DEATH */
	callbacks.VMStart = &cbVMStart;


	error = jvmti->SetEventCallbacks(&callbacks, (jint) sizeof(callbacks));
	check_jvmti_error(jvmti, error, "Cannot set jvmti callbacks");

	error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_START,
			(jthread) NULL);
	check_jvmti_error(jvmti, error, "Cannot set event notification");
	error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_INIT,
			(jthread) NULL);
	check_jvmti_error(jvmti, error, "Cannot set event notification");
	error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH,
			(jthread) NULL);
	check_jvmti_error(jvmti, error, "Cannot set event notification");

	/* Here we create a raw monitor for our use in this agent to
	 *   protect critical sections of code.
	 */
	error = jvmti->CreateRawMonitor("agent data", &(gdata->lock));
	check_jvmti_error(jvmti, error, "Cannot create raw monitor");

	/* We return JNI_OK to signify success */
	return JNI_OK;
}
