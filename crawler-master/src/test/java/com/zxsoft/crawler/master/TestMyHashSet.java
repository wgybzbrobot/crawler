package com.zxsoft.crawler.master;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class TestMyHashSet {
	
	class Foo {
		String u;
		int p;
		public Foo(String u, int p){
			this.u = u;
			this.p = p;
		}
	}
	
	@Test
	public void testHashSet() {
		Foo foo1 = new Foo("foo", 1);
		Foo foo2 = new Foo("foo", 1);
		Foo foo3 = new Foo("foo", 1);
		
		Set<Foo> set = new HashSet<Foo>();
		set.add(foo1);
		set.add(foo2);
		set.add(foo3);
		
		for (Foo foo: set) {
	        System.out.println(foo.p);
        }
	}
}