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
package com.dragon4.owo.ar_trace.ARCore.render;

/**
 * @author daniele
 *
 */
// 벡터 연산에 사용 할 클래스
public class MixVector {
	// 3방향
	public float x;
	public float y;
	public float z;

	// 생성자
	public MixVector() {
		this(0, 0, 0);
	}

	// 벡터형 객체 복사
	public MixVector(MixVector v) {
		this(v.x, v.y, v.z);
	}

	// 행렬로 받을때
	public MixVector(float v[]) {
		this(v[0], v[1], v[2]);
	}

	// 각각의 값으로 받을 때
	public MixVector(float x, float y, float z) {
		set(x, y, z);
	}

	// 인자로 받은 객체와 같은 벡터값인지 조사
	@Override
	public boolean equals(Object obj) {
		MixVector v = (MixVector) obj;
		return (v.x == x && v.y == y && v.z == z);
	}

	// 인자로 받은 각 값과 벡터의 값이 같은지 조사
	public boolean equals(float x, float y, float z) {
		return (this.x == x && this.y == y && this.z == z);
	}

	// 문자열 형태로 벡터의 정보를 출력
	@Override
	public String toString() {
		return "<" + x + ", " + y + ", " + z + ">";
	}

	// 벡터값 세팅. 객체로 받음
	public void set(MixVector v) {
		set(v.x, v.y, v.z);
	}

	// 각각의 수치로 세팅
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// 각각의 수치를 벡터 객체에 더함
	public void add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	// 인자로 받은 벡터 객체의 값을 더함
	public void add(MixVector v) {
		add(v.x, v.y, v.z);
	}

	// 각각의 수치를 벡터 객체에서 뺌
	public void sub(float x, float y, float z) {
		add(-x, -y, -z);
	}

	// 인자로 받은 벡터 객체의 값을 뺌
	public void sub(MixVector v) {
		add(-v.x, -v.y, -v.z);
	}

	// 인자로 받은 값을 벡터의 각 값에 더함
	public void mult(float s) {
		x *= s;
		y *= s;
		z *= s;
	}

	// 인자로 받은 값을 벡터의 각 값에서 뺌
	public void divide(float s) {
		x /= s;
		y /= s;
		z /= s;
	}

	// 백터의 길이를 리턴
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	// 벡터의 2차원 길이를 리턴
	public float length2D() {
		return (float) Math.sqrt(x * x + z * z);
	}

	// 벡터의 노름(norm)값을 리턴
	public void norm() {
		divide(length());
	}

	// 인자로 넣은 벡터와의 내적을 구함
	public float dot(MixVector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	// 두 벡터의 외적을 구해서 저장
	public void cross(MixVector u, MixVector v) {
		float x = u.y * v.z - u.z * v.y;
		float y = u.z * v.x - u.x * v.z;
		float z = u.x * v.y - u.y * v.x;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// 인자로 받은 벡터와의 곱을 계산
	public void prod(Matrix m) {
		float xTemp = m.a1 * x + m.a2 * y + m.a3 * z;
		float yTemp = m.b1 * x + m.b2 * y + m.b3 * z;
		float zTemp = m.c1 * x + m.c2 * y + m.c3 * z;

		x = xTemp;
		y = yTemp;
		z = zTemp;
	}
}
