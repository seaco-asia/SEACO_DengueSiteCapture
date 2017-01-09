package com.seaco.denguesitecapture.activities;
/**
 * Upload Listener
 *
 */
public interface ProgressListener {
	/**
	 * This method updated how much data size uploaded to server
	 * @param num
	 */
	void transferred(long num);
}
