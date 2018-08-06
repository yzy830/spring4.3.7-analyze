package com.gerald.analyze.annotation.method;

import com.gerald.analyze.annotation.StandardAnnotationMetadata.B;
import com.gerald.analyze.annotation.StandardAnnotationMetadata.C;

public class SubClass2 extends SubClass {
	@Override
	@C
	public void test() {
		System.out.println("sub class 2");
	}
}
