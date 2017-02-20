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

/**
 * edited by joyeongje on 2016. 12. 31..
 */


package com.dragon4.owo.ar_trace.ARCore.Marker;


import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcelable;

import com.dragon4.owo.ar_trace.ARCore.MixView;
import com.dragon4.owo.ar_trace.ARCore.data.DataSource;
import com.dragon4.owo.ar_trace.ARCore.gui.PaintScreen;

import java.io.Serializable;


// 소셜 마커 클래스. 마커에서 확장(상속)
public class SocialARMarker extends ARMarker implements Serializable {
	

	public final int MAX_OBJECTS=20;	// 최대 객체 수
	private String flag;
	// 생성자. 타이틀과 위도, 경도, 고도, 그리고 URL과 데이터 소스를 인자로 받는다
	public SocialARMarker(String title, double latitude, double longitude,
						  double altitude, String URL, DataSource.DATASOURCE datasource, String flag) {
		super(title, latitude, longitude, altitude, URL, datasource);
		this.flag = flag;
	}



	// 마커 갱신
	@Override
	public void update(Location curGPSFix) {

		//0.35 radians ~= 20 degree
		//0.85 radians ~= 45 degree
		//minAltitude = sin(0.35)
		//maxAltitude = sin(0.85)
		double altitude = curGPSFix.getAltitude()+Math.sin(0.35)*distance+Math.sin(0.4)*(distance/(MixView.getDataView().getRadius()*1000f/distance));
		mGeoLoc.setAltitude(altitude);
		super.update(curGPSFix);

	}

	// 페인트 스크린에 마커 출력
	@Override
	public void draw(PaintScreen dw) {

		// 텍스트 블록을 그린다
		drawTextBlock(dw,datasource);

		// 보여지는 상황이라면
		if (isVisible) {
			float maxHeight = Math.round(dw.getHeight() / 10f) + 1;	// 최대 높이 계산
			// 데이터 소스의 비트맵 파일을 읽어온다
			Bitmap bitmap = DataSource.getBitmap(flag);
			
			// 비트맵 파일이 읽혔다면 적절한 위치에 출력
			if(bitmap!=null) {
				dw.paintBitmap(bitmap, cMarker.x - maxHeight/1.5f, cMarker.y - maxHeight/1.5f);
			}
			else {	// 비트맵 파일을 갖지 않는 마커의 경우
				dw.setStrokeWidth(maxHeight / 10f);
				dw.setFill(false);
				dw.setColor(DataSource.getColor(datasource));
				dw.paintCircle(cMarker.x, cMarker.y, maxHeight / 1.5f);
			}
		}
	}

	// 최대 객체 수를 리턴
	@Override
	public int getMaxObjects() {
		return MAX_OBJECTS;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
}
