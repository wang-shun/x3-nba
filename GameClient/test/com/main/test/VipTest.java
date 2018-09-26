package com.main.test;

import com.ftkj.console.VipConsole;

public class VipTest extends TestBase {

	public static void main(String[] args) {
		System.err.println(VipConsole.getLevelByAddMoney(1));
		System.err.println(VipConsole.getLevelByAddMoney(5));
		System.err.println(VipConsole.getLevelByAddMoney(6));
		System.err.println(VipConsole.getLevelByAddMoney(7));
		System.err.println(VipConsole.getLevelByAddMoney(30));
		System.err.println(VipConsole.getLevelByAddMoney(62));
		System.err.println(VipConsole.getLevelByAddMoney(290));
		System.err.println(VipConsole.getLevelByAddMoney(300));
		System.err.println(VipConsole.getLevelByAddMoney(501));
		System.err.println(VipConsole.getLevelByAddMoney(699));
	}
}
