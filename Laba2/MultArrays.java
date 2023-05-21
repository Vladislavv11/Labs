package org.example;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.concurrent.ExecutionException;

public class MultArrays {
    private static final String KERNEL =
            "kernel void mul_arrays(global const float *a, global const float *b, global float *answer) {"
                    + "unsigned int xid = get_global_id(0); answer[xid] = a[xid] * b[xid]; }";

    private static final float[] LEFT_ARRAY = { 1F, 3F, 5F, 7F};
    private static final float[] RIGHT_ARRAY = { 2F, 4F, 6F, 8F};

    private static void printSequence(String label, FloatBuffer sequence, PrintStream to) {
        to.print(label);
        to.print(": [ ");
        for (int i = 0; i < sequence.limit(); i++) {
            to.print(' ');
            to.print(Float.toString(sequence.get(i)));
            to.print(' ');

        }
        to.println(" ]");
    }

    public static void main(String[] args) {
        try (CLRuntime cl = new CLRuntime(); MemoryStack stack = MemoryStack.stackPush();) {
            CLRuntime.Platform platform = cl.getPlatforms().first();
            CLRuntime.Device device = platform.getDefault();
            try (CLRuntime.Context context = device.createContext();
                 CLRuntime.Program program = context.createProgramWithSource(KERNEL)) {

                FloatBuffer lhs = stack.floats(LEFT_ARRAY);
                FloatBuffer rhs = stack.floats(RIGHT_ARRAY);

                printSequence("Left hand statement: ", lhs, System.out);
                printSequence("Right hand statement: ", rhs, System.out);

                int gws = LEFT_ARRAY.length * Float.BYTES;

                CLRuntime.CommandQueue cq = program.getCommandQueue();

                final CLRuntime.VideoMemBuffer first = cq.hostPtrReadBuffer(MemoryUtil.memAddressSafe(lhs), gws);
                final CLRuntime.VideoMemBuffer second = cq.hostPtrReadBuffer(MemoryUtil.memAddressSafe(rhs), gws);
                final CLRuntime.VideoMemBuffer answer = cq.createReadWriteBuffer(gws);

                cq.flush();

                CLRuntime.Kernel sumVectors = program.createKernel("mul_arrays");
                sumVectors.arg(first).arg(second).arg(answer).executeAsDataParallel(gws);

                ByteBuffer result = MemoryUtil.memAlloc(answer.getCapacity());
                cq.readVideoMemory(answer, result);

                printSequence("Result: ", result.asFloatBuffer(), System.out);

            } catch (ExecutionException exc) {
                System.err.println(exc.getMessage());
                System.exit(-1);
            }
        }
    }
}
