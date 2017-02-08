/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file is part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package com.dragon4.owo.ar_trace.ARCore;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

// 유틸 클래스
public class MixUtils {
	// JSON 데이터의 파싱 작업을 수행
	public static String parseAction(String action) {
		Log.i("parseaction",action);
		return (action.substring(action.indexOf(':') + 1, action.length()))
				.trim();
	}

	// 거리의 단위 변환
	public static String formatDist(float meters) {
		if (meters < 1000) {
			return ((int) meters) + "m";
		} else if (meters < 10000) {
			return formatDec(meters / 1000f, 1) + "km";
		} else {
			return ((int) (meters / 1000f)) + "km";
		}
	}

	// 수치의 변환. 소수점 한자리 까지만 수용한다
	static String formatDec(float val, int dec) {
		int factor = (int) Math.pow(10, dec);

		int front = (int) (val );
		int back = (int) Math.abs(val * (factor) ) % factor;

		return front + "." + back;
	}

	// 점이 지정된 공간 내에 있는지 판별한다
	public static boolean pointInside(float P_x, float P_y, float r_x,
		float r_y, float r_w, float r_h) {
		return (P_x > r_x && P_x < r_x + r_w && P_y > r_y && P_y < r_y + r_h);
	}

	// 중점과 점 사이의 각도를 구한다
	public static float getAngle(float center_x, float center_y, float post_x,
			float post_y) {
		float tmpv_x = post_x - center_x;
		float tmpv_y = post_y - center_y;
		float d = (float) Math.sqrt(tmpv_x * tmpv_x + tmpv_y * tmpv_y);
		float cos = tmpv_x / d;
		float angle = (float) Math.toDegrees(Math.acos(cos));

		angle = (tmpv_y < 0) ? angle * -1 : angle;

		return angle;
	}

	public static String getDateString(long createDate) {
		long timeDiff = System.currentTimeMillis() - createDate;

		if(timeDiff < 60000)
			return "방금";
		else if(timeDiff < 3600000)
			return (timeDiff/(60000))+"분 전";
		else if(timeDiff < 86400000)
			return (timeDiff/(3600000))+"시간 전";
		else if(timeDiff < 2592000000L)
			return (timeDiff/(86400000))+"일 전";
		else if(timeDiff >= 31104000000L)
			return (timeDiff/(2592000000L))+"달 전";
		else
			return (timeDiff/(31104000000L))+"년 전";
	}

	// 경도랑 위도로 md5 해싱처리.
	public static String makeHashStringMD5(double lat,double lon) {

		String MD5 = "";
		String str = String.valueOf(lat) + String.valueOf(lon);
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();

		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			MD5 = null;
		}
		return MD5;

	}

	// 경도랑 위도로 SHA-256
	public String makeHashStringHA256(double lat,double lon){
		String SHA = "";
		String str = String.valueOf(lat) + String.valueOf(lon);
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(str.getBytes());
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();

		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
			SHA = null;
		}
		return SHA;
	}
}
