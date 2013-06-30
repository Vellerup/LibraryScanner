package com.example.libraryscanner;



import java.io.IOException;
import java.nio.charset.Charset;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;
public class NfcReader {

	// Used in log messages.
		private static final String TAG = NfcReader.class.getSimpleName();
		
		
		private NfcV NfcVTag;
		
		/**
		 * Constructor that tags a discovered tag and connects to the NFC-V tag.
		 * 
		 * @param tag
		 */
		public NfcReader(Tag tag) {
			NfcVTag = NfcV.get(tag);
			try {
				NfcVTag.connect();
			}
			catch (IOException e) {
				Log.e(TAG, "IOException while reading NfcV message...", e);
			}
		}
		
		/**
		 * Finalize that closes the connection to the tag when the object is destroyed.
		 */
		protected void finalize() throws Throwable {
			this.close();
			super.finalize();
		}
		
		/**
		 * Closes the connection with the current tag.
		 */
		public void close() {
			if (NfcVTag != null) {
				try {
					NfcVTag.close();
				}
				catch (IOException e) {
					Log.e(TAG, "Error closing tag...", e);
				}
			}
		}
		
		/**
		 * Gets information about the connected tag (DSFID, AFI and VICC MEMORY).
		 * 
		 * @return int[]
		 * 	Containing AFI, number of blocks and block size.
		 */
		public byte[] getInfo() {		
			try {
				byte[] data = NfcVTag.transceive((new byte[] {0, 43}));
				
				// If first 3 bits is set all information is available. This should be changed 
				// to use bit masks, so we only fetch information availble based on flag.
				if (data[1] == (byte)15) {
					byte afi = data[11];
					byte blocks = data[12];
					byte blockSize = data[13];
					return new byte[]{ afi, blocks, blockSize};
				}
			}
			catch (IOException e) {
				Log.e(TAG, "IOException while reading NfcV message...", e);
			}		
			return null;
		}

		/**
		 * Read a single block.
		 * 
		 * @param blockId
		 * 	The id of the block to fetch index from 0 to number of blocks.
		 * @return String
		 * 	The block encode as a string.
		 */
		public String readBlockAsString(byte blockId) {
			byte[] block = this.readBlock(blockId);
			return new String(block, Charset.forName("US-ASCII"));
		}
		
		/**
		 * Read a single block from the tag.
		 * 
		 * @param blockId
		 * 	The id of the block to fetch index from 0 to number of blocks.
		 * @return byte[]
		 * 	Data block as byte array.
		 */
		public byte[] readBlock(byte blockId) {
			try {
				// where 0 is flag, 32 is reading command and the block number.	
				byte[] data = NfcVTag.transceive((new byte[] {0, 32, blockId}));
				return data;
			}
			catch (IOException e) {
				Log.e(TAG, "IOException while reading NfcV message...", e);
			}		
			return null;
		}
		
		/**
		 * Get multiply blocks from the tag.
		 * 
		 * @param blockId
		 * 	Start block to read from.
		 * @param numberOfBlocks
		 * 	The number of blocks to read. 
		 * @return String
		 * 	The blocks as a formated string.
		 */
		public String readBlocksAsString(byte blockId, byte numberOfBlocks) {
			byte[] data = readBlocks(blockId, numberOfBlocks);
			return new String(data, Charset.forName("US-ASCII"));
		}
		
		/**
		 * Get multiply blocks from the tag.
		 * 
		 * @param blockId
		 * 	Start block to read from.
		 * @param numberOfBlocks
		 * 	The number of blocks to read. 
		 * @return byte[]
		 * 	The blocks a byte array.
		 */
		public byte[] readBlocks(byte blockId, byte numberOfBlocks) {
			try {
				byte[] data = NfcVTag.transceive((new byte[] {0, 35, blockId, numberOfBlocks}));
				return data;
			}
			catch (IOException e) {
				Log.e(TAG, "IOException while reading NfcV message...", e);
			}		
			return null;
		}
		
		/**
		 * Write value to AFI on the tag.
		 * 
		 * @param value
		 * 	The byte value to write to the tag.
		 * @return
		 */
		public byte[] writeAfi(byte value) {
			try {
				byte[] data = NfcVTag.transceive((new byte[] {0, 39, value}));
				return data;
			}
			catch (IOException e) {
				Log.e(TAG, "IOException while reading NfcV message...", e);
			}		
			return null;
		}
}
