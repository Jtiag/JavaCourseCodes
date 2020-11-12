package java0.conc0303.tool;

import java0.conc0303.tool.domain.StudentTask;

import java.util.concurrent.Phaser;

/***
 *  下面说说Phaser的高级用法，在Phaser内有2个重要状态，分别是phase和parties。
 *  phase就是阶段，初值为0，当所有的线程执行完本轮任务，同时开始下一轮任务时，
 *  意味着当前阶段已结束，进入到下一阶段，phase的值自动加1。
 *  parties就是线程，party=4就意味着Phaser对象当前管理着4个线程。Phaser还有一个重要的方法经常需要被重载，
 *  那就是boolean onAdvance(int phase, int registeredParties)方法。此方法有2个作用：
 *  1、当每一个阶段执行完毕，此方法会被自动调用，因此，重载此方法写入的代码会在每个阶段执行完毕时执行，
 *  相当于CyclicBarrier的barrierAction。
 *  2、当此方法返回true时，意味着Phaser被终止，因此可以巧妙的设置此方法的返回值来终止所有线程。
 * @author null
 */
public class PhaserDemo extends Phaser {
    /**
     * 在每个阶段执行完成后回调的方法
     *
     * @param phase
     * @param registeredParties
     * @return
     */
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {

        switch (phase) {
            case 0:
                return studentArrived();
            case 1:
                return finishFirstExercise();
            case 2:
                return finishSecondExercise();
            case 3:
                return finishExam();
            default:
                return true;
        }

    }

    private boolean studentArrived() {
        System.out.println("学生准备好了,学生人数：" + getRegisteredParties());
        return false;
    }

    private boolean finishFirstExercise() {
        System.out.println("第一题所有学生做完");
        return false;
    }

    private boolean finishSecondExercise() {
        System.out.println("第二题所有学生做完");
        return false;
    }

    private boolean finishExam() {
        System.out.println("第三题所有学生做完，结束考试");
        return true;
    }

    /**
     * 题目：5个学生参加考试，一共有三道题，要求所有学生到齐才能开始考试
     * ，全部做完第一题，才能继续做第二题，后面类似。
     * <p>
     * Phaser有phase和party两个重要状态，
     * phase表示阶段，party表示每个阶段的线程个数，
     * 只有每个线程都执行了phaser.arriveAndAwaitAdvance();
     * 才会进入下一个阶段，否则阻塞等待。
     * 例如题目中5个学生(线程)都调用phaser.arriveAndAwaitAdvance();就进入下一题
     *
     * @author null
     */
    public static void main(String[] args) {
        PhaserDemo phaser = new PhaserDemo();
        StudentTask[] studentTask = new StudentTask[5];
        for (int i = 0; i < studentTask.length; i++) {
            studentTask[i] = new StudentTask(phaser);
            // 注册一次表示phaser维护的线程个数
            phaser.register();
        }

        Thread[] threads = new Thread[studentTask.length];
        for (int i = 0; i < studentTask.length; i++) {
            threads[i] = new Thread(studentTask[i], "Student " + i);
            threads[i].start();
        }

        // 等待所有线程执行结束
        for (int i = 0; i < studentTask.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Phaser has finished:" + phaser.isTerminated());

    }

}
