package com.google.devrel.gmscore.tools.apk.arsc;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * Thrown when {@link UnknownChunk} self-reported size is clearly bogus.
 */
public class BogusUnknownChunkException extends RuntimeException {
	private final UnknownChunk unknown;
	private final ByteBuffer buffer;

	/**
	 * @param unknown
	 * 		Bogus unknown chunk.
	 * @param buffer
	 * 		Buffer read from.
	 */
	public BogusUnknownChunkException(@Nonnull UnknownChunk unknown, @Nonnull ByteBuffer buffer) {
		this.unknown = unknown;
		this.buffer = buffer;
	}

	/**
	 * Skips to the end of the containing chunk.
	 *
	 * @param containingChunk
	 * 		Chunk containing the {@link #getUnknown() unknown chunk}.
	 * @param start
	 * 		Start position of the unknown chunk.
	 */
	public void skipToEndOfContainingChunk(@Nonnull Chunk containingChunk, int start) {
		int containingSize = containingChunk.getOriginalChunkSize();
		buffer.position(start + containingSize);

		int patchedChunkSize = containingSize - containingChunk.getHeaderSize();
		unknown.setPatchedSize(patchedChunkSize);
	}

	@Nonnull
	public UnknownChunk getUnknown() {
		return unknown;
	}
}
