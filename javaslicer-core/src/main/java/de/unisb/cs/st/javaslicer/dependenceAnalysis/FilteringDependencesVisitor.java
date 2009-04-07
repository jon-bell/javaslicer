package de.unisb.cs.st.javaslicer.dependenceAnalysis;

import de.hammacher.util.Filter;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadMethod;
import de.unisb.cs.st.javaslicer.variables.Variable;


public class FilteringDependencesVisitor<InstanceType> implements
        DependencesVisitor<InstanceType> {

    private final Filter<? super InstanceType> filter;
    private final DependencesVisitor<InstanceType> visitor;


    public FilteringDependencesVisitor(Filter<? super InstanceType> filter,
            DependencesVisitor<InstanceType> visitor) {
        this.filter = filter;
        this.visitor = visitor;
    }

    public void discardPendingDataDependence(InstanceType from,
            Variable var, DataDependenceType type) {
        if (this.filter.filter(from))
            this.visitor.discardPendingDataDependence(from, var, type);
    }

    public void visitControlDependence(InstanceType from, InstanceType to) {
        if (this.filter.filter(from) && this.filter.filter(to))
            this.visitor.visitControlDependence(from, to);
    }

    public void visitDataDependence(InstanceType from,
            InstanceType to, Variable var, DataDependenceType type) {
        if (this.filter.filter(from) && this.filter.filter(to))
            this.visitor.visitDataDependence(from, to, var, type);
    }

    public void visitEnd(long numInstances) {
        this.visitor.visitEnd(numInstances);
    }

    public void visitInstructionExecution(InstanceType instance) {
        if (this.filter.filter(instance))
            this.visitor.visitInstructionExecution(instance);
    }

    public void visitMethodEntry(ReadMethod method) {
        this.visitor.visitMethodEntry(method);
    }

    public void visitMethodLeave(ReadMethod method) {
        this.visitor.visitMethodLeave(method);
    }

    public void visitObjectCreation(long objectId, InstanceType instrInstance) {
        if (this.filter.filter(instrInstance))
            this.visitor.visitObjectCreation(objectId, instrInstance);
    }

    public void visitPendingControlDependence(InstanceType from) {
        if (this.filter.filter(from))
            this.visitor.visitPendingControlDependence(from);
    }

    public void visitPendingDataDependence(InstanceType from, Variable var,
            DataDependenceType type) {
        if (this.filter.filter(from))
            this.visitor.visitPendingDataDependence(from, var, type);
    }

}
