package com.dnarvaez27.line_chart.recursos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import java.awt.Color;

public class UtilidadesLineChart
{
	public static class Colors
	{
		public static final Color GRIS = new Color( 33, 33, 33 );
		public static final Color BLANCO = new Color( 255, 255, 255 );
		public static final Color FUND_ANI = new Color( 229, 215, 202 );
		public static final Color DROPBOX_COLOR = new Color( 0, 139, 211 );
		public static final Color ROJO = new Color( 177, 34, 34 );
		public static final Color MACHO_COLOR = new Color( 0, 150, 150 );
		public static final Color HEMBRA_COLOR = new Color( 250, 0, 50 );
		public static final Color VERDE = new Color( 84, 197, 143 );
		public static final Color AMARILLO = new Color( 210, 180, 10 );
		public static final Color AZUL_ED = new Color( 0, 86, 124 );
		public static final Color CAFE = new Color( 101, 80, 59 );

		public static final Color BLUE = new Color( 0, 188, 212 );
		public static final Color GREEN = new Color( 205, 220, 57 );
		public static final Color ORANGE = new Color( 255, 87, 34 );
		public static final Color PINK = new Color( 233, 30, 99 );
		public static final Color YELLOW = new Color( 255, 193, 7 );

		public static final ArrayList<Color> COLORS_PAWS = new ArrayList<>( Arrays.asList( BLUE, YELLOW, PINK, GREEN, ORANGE ) );
		public static final Color VERDE_INGRESO = new Color( 121, 166, 37 );
	}

	public static String darFormatoNúmeroValor( String valor )
	{
		String text = valor.replace( "$", "" );
		text = text.replace( " ", "" );
		double num = Double.parseDouble( text.replace( ",", "" ) );
		String temp = NumberFormat.getNumberInstance( Locale.US ).format( num );
		valor = "$ " + temp;

		return valor;
	}

	public static double round( double valor, int lugares )
	{
		if( lugares < 0 )
		{
			throw new IllegalArgumentException( );
		}
		if( valor == 0 || Double.isNaN( valor ) || Double.isInfinite( valor ) )
		{
			return 0;
		}
		BigDecimal bd = new BigDecimal( valor );
		bd = bd.setScale( lugares, RoundingMode.HALF_UP );
		return bd.doubleValue( );
	}
}