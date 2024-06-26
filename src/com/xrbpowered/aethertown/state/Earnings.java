package com.xrbpowered.aethertown.state;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.AetherTown.settings;

import java.util.Random;

import com.xrbpowered.aethertown.world.stars.WorldTime;

public class Earnings {

	public static final int workXP = 2;
	
	private Earnings() {
	}

	public static boolean isEmpty() {
		return player.ubiCollectedDay>=WorldTime.getDay() && player.earnings==0;
	}
	
	private static void reportRow(StringBuilder sb, boolean total, String name, String value, boolean zero) {
		if(total)
			sb.append("<tr class=\"total\"><td class=\"w\">");
		else
			sb.append("<tr><td>");
		sb.append(name);
		sb.append("</td>");
		sb.append(String.format("<td class=\"v\"><span class=\"%s\">%s</span></td></tr>", zero ? "d" : "w", value));
	}

	private static void reportRowCost(StringBuilder sb, String name, int cost) {
		reportRow(sb, false, name, formatCost(cost), cost==0);
	}

	private static void reportRowTotalCost(StringBuilder sb, int cost) {
		reportRow(sb, true, "TOTAL", formatCost(cost), cost==0);
	}

	public static String collect() {
		StringBuilder sb = new StringBuilder();
		sb.append("<p>Collected earnings:</p><table style=\"width:100%\">");
		
		int today = WorldTime.getDay();
		int total = 0;
		if(player.ubiCollectedDay<0) {
			player.ubiCollectedDay = 0;
			total += settings.initialPay;
			reportRowCost(sb, "Initial payment", settings.initialPay);
		}
		
		int days = today - player.ubiCollectedDay;
		if(days>=0) {
			int ubi = days * settings.dailyPay;
			total += ubi;
			reportRowCost(sb, String.format("UBI (%d %s)", days, days!=1 ? "days" : "day"), ubi);
		}
		
		if(player.earnings>=0) {
			total += player.earnings;
			reportRowCost(sb, "Work payouts", player.earnings);
			player.earnings = 0;
		}
		
		reportRowTotalCost(sb, total);
		sb.append("</table>");
		
		player.cash += total;
		player.ubiCollectedDay = today;
		return sb.toString();
	}

	public static String work() {
		long t = Math.round(WorldTime.time/WorldTime.minute);
		Random random = new Random(t);
		
		StringBuilder sb = new StringBuilder();
		int ins = -player.addInspiration(-50);
		int xp = player.getXP();
		int addXP = player.addXP(workXP);
		if(ins>0)
			sb.append(String.format("<p>&minus;%d inspiration, %+d XP</p>", ins, addXP));
		else
			sb.append(String.format("<p>No inspiration, %+d XP</p>", addXP));
		sb.append("<p>Earned:</p><table style=\"width:100%\">");
		
		int total = 0;
		int c = random.nextInt(251)+500;
		total += c;
		reportRowCost(sb, "Baseline", c);
		
		c = (random.nextInt(16)+15) * ins;
		total += c;
		reportRowCost(sb, String.format("from %d INS", ins), c);

		c = (int) Math.round((random.nextDouble()*0.5+0.5)*diminish(xp, 1500, 2500));
		total += c;
		reportRowCost(sb, String.format("from %d XP", xp), c);

		reportRowTotalCost(sb, total);
		sb.append("</table><p>&nbsp<br>You can collect earnings at any post office or hotel.</p>");
		
		player.earnings += total;
		return sb.toString();
	}

	private static double diminish(double x, double max, double lambda) {
		return max*x / (x+lambda);
	}
	
	public static int parseCost(String s) {
		return Math.round(Float.parseFloat(s) * 100);
	}
	
	public static String formatCost(int cost) {
		return String.format("%.2f", cost/100f);
	}
	
}
