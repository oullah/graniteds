/**
 *   GRANITE DATA SERVICES
 *   Copyright (C) 2006-2014 GRANITE DATA SERVICES S.A.S.
 *
 *   This file is part of the Granite Data Services Platform.
 *
 *   Granite Data Services is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   Granite Data Services is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 *   General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 *   USA, or see <http://www.gnu.org/licenses/>.
 */
package org.granite.messaging.jmf.codec.std.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;

import org.granite.messaging.jmf.DumpContext;
import org.granite.messaging.jmf.InputContext;
import org.granite.messaging.jmf.OutputContext;
import org.granite.messaging.jmf.codec.std.SqlDateCodec;
import org.granite.messaging.jmf.codec.std.impl.util.LongUtil;

/**
 * @author Franck WOLFF
 */
public class SqlDateCodecImpl extends AbstractStandardCodec<Date> implements SqlDateCodec {

	public int getObjectType() {
		return JMF_SQL_DATE;
	}

	public Class<?> getObjectClass() {
		return Date.class;
	}

	public void encode(OutputContext ctx, Date v) throws IOException {
		final OutputStream os = ctx.getOutputStream();
		
		os.write(JMF_SQL_DATE);
		LongUtil.encodeLong(ctx, v.getTime());
	}

	public Date decode(InputContext ctx, int parameterizedJmfType) throws IOException {
		return new Date(LongUtil.decodeLong(ctx));
	}
	
	public void dump(DumpContext ctx, int parameterizedJmfType) throws IOException {
		int jmfType = ctx.getSharedContext().getCodecRegistry().extractJmfType(parameterizedJmfType);
		
		switch (jmfType) {
		case JMF_SQL_DATE:
			ctx.indentPrintLn(Date.class.getName() + ": " + decode(ctx, parameterizedJmfType));
			break;
		default:
			throw newBadTypeJMFEncodingException(jmfType, parameterizedJmfType);
		}
	}
}
