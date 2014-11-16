package distantshoresmedia.org.keyboard;

import java.util.LinkedList;

/**
 * Created by Fechner on 11/15/14.
 */
static public class PointerQueue {
    private LinkedList<PointerTracker> mQueue = new LinkedList<PointerTracker>();

    public void add(PointerTracker tracker) {
        mQueue.add(tracker);
    }

    public int lastIndexOf(PointerTracker tracker) {
        LinkedList<PointerTracker> queue = mQueue;
        for (int index = queue.size() - 1; index >= 0; index--) {
            PointerTracker t = queue.get(index);
            if (t == tracker)
                return index;
        }
        return -1;
    }

    public void releaseAllPointersOlderThan(PointerTracker tracker, long eventTime) {
        LinkedList<PointerTracker> queue = mQueue;
        int oldestPos = 0;
        for (PointerTracker t = queue.get(oldestPos); t != tracker; t = queue.get(oldestPos)) {
            if (t.isModifier()) {
                oldestPos++;
            } else {
                t.onUpEvent(t.getLastX(), t.getLastY(), eventTime);
                t.setAlreadyProcessed();
                queue.remove(oldestPos);
            }
        }
    }

    public void releaseAllPointersExcept(PointerTracker tracker, long eventTime) {
        for (PointerTracker t : mQueue) {
            if (t == tracker)
                continue;
            t.onUpEvent(t.getLastX(), t.getLastY(), eventTime);
            t.setAlreadyProcessed();
        }
        mQueue.clear();
        if (tracker != null)
            mQueue.add(tracker);
    }

    public void remove(PointerTracker tracker) {
        mQueue.remove(tracker);
    }

    public boolean isInSlidingKeyInput() {
        for (final PointerTracker tracker : mQueue) {
            if (tracker.isInSlidingKeyInput())
                return true;
        }
        return false;
    }
}