package com.ftkj;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Marc.Wang 2011-9-22 下午04:36:43
 * 功能：
 */
public interface ExcuteReturnData {
	//
	Object getObject(int type,InputStream is,boolean isZip)throws IOException;
}
