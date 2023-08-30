package com.xunmo.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.codec.JsonJacksonCodec;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 替换Redisson的序列化对象JsonJacksonCodec，兼容原有RedisTemplate的对象序列化。
 *
 * @author yangzihe
 * @date 2022/12/13
 */
public class RedissonCodec extends BaseCodec {

	protected final ObjectMapper mapObjectMapper;

	/**
	 * @see JsonJacksonCodec
	 */
	private final Encoder encoder = new Encoder() {
		@Override
		public ByteBuf encode(Object in) throws IOException {
			ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
			try {
				ByteBufOutputStream os = new ByteBufOutputStream(out);
				mapObjectMapper.writeValue((OutputStream) os, in);
				return os.buffer();
			}
			catch (IOException e) {
				out.release();
				throw e;
			}
			catch (Exception e) {
				out.release();
				throw new IOException(e);
			}
		}
	};

	private final Decoder<Object> decoder = new Decoder<Object>() {
		@Override
		public Object decode(ByteBuf buf, State state) throws IOException {
			return mapObjectMapper.readTree(new ByteBufInputStream(buf));
		}
	};

	public RedissonCodec(ObjectMapper mapObjectMapper) {
		this(mapObjectMapper, true);
	}

	public RedissonCodec(ObjectMapper mapObjectMapper, boolean copy) {
		if (copy) {
			this.mapObjectMapper = mapObjectMapper.copy();
		}
		else {
			this.mapObjectMapper = mapObjectMapper;
		}
	}

	@Override
	public Decoder<Object> getValueDecoder() {
		return decoder;
	}

	@Override
	public Encoder getValueEncoder() {
		return encoder;
	}

	@Override
	public ClassLoader getClassLoader() {
		if (mapObjectMapper.getTypeFactory().getClassLoader() != null) {
			return mapObjectMapper.getTypeFactory().getClassLoader();
		}

		return super.getClassLoader();
	}

}
