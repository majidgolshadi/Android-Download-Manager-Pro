package com.golshadi.majid.core.chunkWorker;


public class ConnectionWatchDog extends Thread
{
	/** Rate at which timer is checked */
	protected int m_rate = 100;
	
	/** Length of timeout */
	private int m_length;

	/** Time elapsed */
	private int m_elapsed;
	
	/** under control Thread **/
	private AsyncWorker underControl;

	/**
	  * Creates a timer of a specified length
	  * @param	length	Length of time before timeout occurs
	  */
	public ConnectionWatchDog(int length, AsyncWorker thread)
	{
		// Assign to member variable
		m_length = length;

		// Set time elapsed
		m_elapsed = 0;
		
		// set under control thread
		underControl= thread;
	}

	
	/** Resets the timer back to zero */
	public synchronized void reset()
	{
		m_elapsed = 0;
	}

	/** Performs timer specific code */
	public void run()
	{
		// Keep looping
		while (!Thread.currentThread().isInterrupted())
		{
			// Put the timer to sleep
			try
			{ 
				Thread.sleep(m_rate);
			}
			catch (InterruptedException ioe) 
			{
				break;
			}

			// Use 'synchronized' to prevent conflicts
			synchronized ( this )
			{
				// Increment time remaining
				m_elapsed += m_rate;

				// Check to see if the time has been exceeded
				if (m_elapsed > m_length)
				{
					// Trigger a timeout
					timeout();
				}
			}

		}
	}

	// Override this to provide custom functionality
	public void timeout()
	{
		underControl.connectionTimeOut();
	}

}
