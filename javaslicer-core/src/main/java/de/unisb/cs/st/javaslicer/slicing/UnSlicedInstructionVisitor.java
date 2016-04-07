package de.unisb.cs.st.javaslicer.slicing;

import java.util.HashSet;

import sun.security.jca.GetInstance.Instance;
import de.unisb.cs.st.javaslicer.common.classRepresentation.Instruction;
import de.unisb.cs.st.javaslicer.common.classRepresentation.InstructionType;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadClass;
import de.unisb.cs.st.javaslicer.common.classRepresentation.ReadMethod;
import de.unisb.cs.st.javaslicer.common.classRepresentation.instructions.MethodInvocationInstruction;
import de.unisb.cs.st.javaslicer.dependenceAnalysis.DependencesVisitorAdapter;
import de.unisb.cs.st.javaslicer.slicing.Slicer.SlicerInstance;

public class UnSlicedInstructionVisitor extends DependencesVisitorAdapter<SlicerInstance> {

    private HashSet<Instruction> insnsTraced = new HashSet<Instruction>();

    @Override
    public void visitInstructionExecution(SlicerInstance instance) throws InterruptedException {
        if (instance.getInstruction().getType() == InstructionType.METHODINVOCATION) {
            insnsTraced.add(instance.getInstruction());
        }
    }

    public void visitInstructionOnSlice(Instruction instruction) {
        if (instruction.getType() == InstructionType.METHODINVOCATION) {
            insnsTraced.remove(instruction);
        }
    }
    
    public HashSet<Instruction> getInsnsTraced() {
        return insnsTraced;
    }
}
