package org.xith3d.utility.memory;

import org.jagatoo.util.nio.BufferUtils;
import org.xith3d.utility.logging.X3DLog;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by smeier on 2/11/15.
 * A singleton that manages a growable pool of ByteBuffers for use in taking screenshots.
 * Using the pool should increase performance over repeated instantiation-destruction,
 * and prevent a memory leak related to generation of ByteBuffers in Windows.
 *
 * The public methods are considered thread-safe.
 */
public class ByteBufferPool {
	private static ByteBufferPool singleton;

	/**
	 * Retrieves the singleton instance of the ByteBufferPool, and creates a new instance of one does not already exist.
	 * @return the singleton instance of the ByteBufferPool.
	 */
	public static ByteBufferPool getSingleton() {
		if (singleton == null) {
			singleton = new ByteBufferPool();
		}
		return singleton;
	}

	private List<ByteBuffer> availableDroplets;
	private int leasedDroplets;

	public ByteBufferPool() {
		availableDroplets = new ArrayList<ByteBuffer>();
		leasedDroplets = 0;
	}

	/**
	 * Leases a ByteBuffer from the pool. A new ByteBuffer instance will be created if one that matches your parameters is not available.
	 * This method is considered thread-safe.
	 * @param capacity the capacity of the requested ByteBuffer, in bytes.
	 * @return a ByteBuffer with the requested capacity. The buffer may contain preexisting data.
	 */
	public synchronized ByteBuffer getDroplet(int capacity) {
		ByteBuffer droplet = null;

		// Try to find an existing, free droplet with the requested capacity before creating a new one
		for (int i=0; (i < availableDroplets.size()) && (droplet == null); i++) {
			if (availableDroplets.get(i).capacity() == capacity) {
				droplet = availableDroplets.remove(i);
			}
		}

		if (droplet == null) {
			droplet = BufferUtils.createByteBuffer(capacity);
			X3DLog.debug("ByteBufferPool created droplet #" + (size() + 1) + " with a capacity of " + capacity + " bytes.");
			if (size() > 9) {
				X3DLog.error("ByteBufferPool has created and leased more than 10 droplets. This is likely an indication of a memory leak.");
			}
		}

		leasedDroplets++;
		return droplet;
	}

	/**
	 * Returns a ByteBuffer to the pool. Do not keep the reference to the object, as another method or thread may use it.
	 * @param droplet the ByteBuffer to return to the pool.
	 */
	public synchronized void releaseDroplet(ByteBuffer droplet) {
		availableDroplets.add(droplet);
		leasedDroplets--;
	}

	/**
	 * @return the total number of droplets owned by the pool, including both available and leased.
	 */
	public synchronized int size() {
		return availableDroplets.size() + leasedDroplets;
	}
}
