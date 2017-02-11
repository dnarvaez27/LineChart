package com.dnarvaez27.line_chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import com.dnarvaez27.line_chart.recursos.UtilidadesLineChart;
import com.dnarvaez27.line_chart.recursos.UtilidadesLineChart.Colors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Clase que modela un panel con una gráfica de lineas
 *
 * @author d.narvaez11
 */
public class LineChart extends JPanel
{
	/**
	 * Modela un dato de la gráfica
	 *
	 * @author d.narvaez11
	 */
	private class Dato implements Comparable<Dato>
	{
		/**
		 * Etiqueta del dato
		 */
		private String textX;

		/**
		 * Valor del dato
		 */
		private double valor;

		/**
		 * Constructor de un Dato
		 *
		 * @param valor Valor del dato
		 * @param textX Etiqueta del dato
		 */
		public Dato( double valor, String textX )
		{
			this.valor = valor;
			this.textX = textX;
		}

		/**
		 * Comparador para ordenar los datos
		 */
		@Override
		public int compareTo( Dato d )
		{
			return Double.compare( valor, d.valor );
		}
	}

	/**
	 * Modela el Listener para el MouseOver de los puntos de interés
	 *
	 * @author d.narvaez11
	 */
	private class DotMouseMotionListener implements MouseMotionListener
	{
		/**
		 * Background del Popup
		 */
		private Color bg;

		/**
		 * Foreground del Popup
		 */
		private Color fg;

		/**
		 * Popup del MouseOver
		 */
		private Popup popup;

		/**
		 * Shape al cual se agrega el listener
		 */
		private Shape shape;

		/**
		 * Valor del Dato
		 */
		private String valor;

		/**
		 * Constructor del Listener<br>
		 * <b>Info: </b> Si es necesario configurar colores de Popup: {@link #setColors(Color, Color)}
		 *
		 * @param shape Shape al cual se agregará el listener
		 * @param valor Valor del punto de la gráfica
		 */
		public DotMouseMotionListener( Shape shape, String valor )
		{
			this.shape = shape;
			this.valor = valor;
		}

		/**
		 * Esconde el Popup si esta visible
		 */
		public void hidePopup( )
		{
			if( popup != null )
			{
				popup.hide( );
			}
		}

		@Override
		public void mouseDragged( MouseEvent e )
		{
		}

		@Override
		public void mouseMoved( MouseEvent e )
		{
			Point p = e.getPoint( );
			// p = new Point( e.getX( ) + 10, e.getY( ) + 10 );
			if( shape.contains( p ) )
			{
				hidePopup( );
				JPanel panel = new JPanel( );
				panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
				JLabel label = new JLabel( valor );
				panel.add( label );

				if( bg != null )
				{
					panel.setBackground( bg );
					label.setForeground( fg );
				}

				int posX = e.getXOnScreen( ) + 10;
				int posY = e.getYOnScreen( ) + 10;

				popup = new PopupFactory( ).getPopup( panel, panel, posX, posY );
				popup.show( );
			}
			else
			{
				hidePopup( );
			}
		}

		/**
		 * Define los colores del Popup
		 *
		 * @param bg Background del Popup
		 * @param fg Foreground del Popup
		 */
		public void setColors( Color bg, Color fg )
		{
			this.bg = bg;
			this.fg = fg;
		}
	}

	/**
	 * Stoke de la linea de la gráfica
	 */
	private static final float graphStroke = 3f;

	private static final long serialVersionUID = 7201050923138852550L;

	/**
	 * Background del LineChart
	 */
	private Color background;

	/**
	 * Arreglo de datos de la gráfica
	 */
	private ArrayList<Dato> datos;

	/**
	 * Foreground del LineChart.
	 */
	private Color foreground;

	/**
	 * La gráfica implementa un formato monetario en el eje y
	 */
	private boolean formatoMoneda;

	/**
	 * Punto de inicio, que cambia con cada linea
	 */
	private Point lastPoint;

	/**
	 * Define si se muestra o no la linea de promedio
	 */
	private boolean lineaPromedio;

	/**
	 * Listeners de los puntos de interes
	 */
	private ArrayList<DotMouseMotionListener> listeners;

	/**
	 * Define si se muestra el nombre del valor en los Popup
	 */
	private boolean muestraNombrePopup;

	/**
	 * Define el color para valores negativos
	 */
	private Color negative;

	/**
	 * Define el color para valores positivos
	 */
	private Color positive;

	/**
	 * Define si se muestra la grafica completa o solo valores positivos
	 */
	private boolean positivoYnegativo;

	/**
	 * Margen en x
	 */
	private int xIni = 50;

	/**
	 * Margen en y
	 */
	private int yIni = 10;

	/**
	 * Constructor de la gráfica
	 */
	public LineChart( )
	{
		datos = new ArrayList<>( );
		listeners = new ArrayList<>( );
		positivoYnegativo = true;

		setPreferredSize( new Dimension( 700, 500 ) );
		setBackground( UtilidadesLineChart.Colors.GRIS );
	}

	/**
	 * Actualiza la gráfica
	 */
	public void actualizar( )
	{
		removeListeners( );

		datos = new ArrayList<>( );
		listeners = new ArrayList<>( );
		positivoYnegativo = true;

		setPreferredSize( new Dimension( 700, 500 ) );
		setBackground( background == null ? UtilidadesLineChart.Colors.GRIS : background );
	}

	/**
	 * Se agrega un dato sin etiqueta
	 *
	 * @param valor Valor a agregar
	 */
	public void agregarDato( double valor )
	{
		datos.add( new Dato( valor, null ) );
	}

	/**
	 * Agrega un dato a la gráfica
	 *
	 * @param valor Valor del dato
	 * @param texto Etiquet del dato
	 */
	public void agregarDato( double valor, String texto )
	{
		datos.add( new Dato( valor, texto ) );
	}

	/**
	 * Retorna el valor en X de una recta en un punto y dados dos puntos
	 * 
	 * @param x1 Coordenada en x del Punto 1
	 * @param y1 Coordenada en y del Punto 1
	 * @param x2 Coordenada en x del Punto 2
	 * @param y2 Coordenada en y del Punto 2
	 * @param y0 Coordenada en y del Punto de Interés
	 * @return <b>x0</b> Coordenada en x del Punto de Interés
	 */
	private double darXdeYenRecta( double x1, double y1, double x2, double y2, double y0 )
	{
		double m = ( y2 - y1 ) / ( x2 - x1 );
		double b = y1 - ( m * x1 );
		double x0 = ( b - y0 ) / ( -m );
		return x0;
	}

	/**
	 * Configura el formato de moneda para el eje Y
	 *
	 * @param formatoMoneda True para configurar el formato de moneda. False de lo contrario
	 */
	public void ejeYFormatoMoneda( boolean formatoMoneda )
	{
		this.formatoMoneda = formatoMoneda;
	}

	/**
	 * Inicializa el punto inicial
	 * 
	 * @param height Height total
	 * @param valor Valor relativo
	 * @param maxValue Valor máximo de la gráfica
	 */
	private void inicializarPoint( double height, double valor, double maxValue )
	{
		double x2 = 0;
		double pixVal = ( ( ( height - ( yIni * 2 ) ) ) * valor ) / maxValue;
		double y2 = 0;
		x2 += xIni;

		if( valor < 0 )
		{
			y2 = ( height ) + Math.abs( pixVal );
		}
		else
		{
			y2 = ( height ) - pixVal;
		}
		lastPoint = new Point( ( int ) x2, ( int ) y2 );
	}

	@Override
	public void paint( Graphics g )
	{
		Graphics2D graphics2d = ( Graphics2D ) g;
		graphics2d.clearRect( 0, 0, getWidth( ), getHeight( ) );

		super.paint( g );

		double maxPos = Collections.max( datos ).valor;
		double maxNeg = Math.abs( Collections.min( datos ).valor );
		double maxValue = Collections.max( new ArrayList<>( Arrays.asList( maxPos, maxNeg ) ) );

		String texto = String.valueOf( maxValue );
		texto = formatoMoneda ? UtilidadesLineChart.darFormatoNúmeroValor( texto ) : texto;

		FontMetrics metrics = graphics2d.getFontMetrics( getFont( ) );
		xIni = metrics.stringWidth( texto + "00" );

		final double height = getHeight( ) - ( yIni );
		final double width = getWidth( ) - ( xIni * 2 );

		double halfHeight = positivoYnegativo ? ( height / 2 ) : ( height - 20 );

		lastPoint = new Point( xIni, ( int ) halfHeight );

		if( lineaPromedio )
		{
			pintarPromedio( graphics2d, width, halfHeight, maxValue );
		}

		pintarEjes( graphics2d, width, positivoYnegativo ? height : height - 20, /* height / 2 */halfHeight );

		pintarDatos( graphics2d, width, height );

		lastPoint = new Point( xIni, ( int ) halfHeight );
	}

	/**
	 * Pinta los datos en la gráfica
	 * 
	 * @param graphics2d Gráfica en la que se pintará
	 * @param width Ancho de la gráfica
	 * @param height Alto de la gráfica
	 */
	private void pintarDatos( Graphics2D graphics2d, final double width, final double height )
	{
		double maxPos = Collections.max( datos ).valor;
		double maxNeg = Math.abs( Collections.min( datos ).valor );
		double maxValue = Collections.max( new ArrayList<>( Arrays.asList( maxPos, maxNeg ) ) );
		int cantidad = datos.size( );

		graphics2d.setColor( background == null ? UtilidadesLineChart.Colors.GRIS : background );
		graphics2d.setStroke( new BasicStroke( graphStroke ) );

		int contador = 0;
		inicializarPoint( positivoYnegativo ? height / 2 : height, datos.get( 0 ).valor, maxValue );
		double halfHeight = positivoYnegativo ? height / 2 : height;

		for( Dato dato : datos )
		{
			final double x1 = lastPoint.getX( );
			final double y1 = lastPoint.getY( );

			double x2 = ( contador++ * width ) / cantidad;
			double pixVal = ( ( ( positivoYnegativo ? halfHeight : halfHeight - 20 ) - ( yIni * 2 ) ) * dato.valor ) / maxValue;
			double y2 = 0;
			x2 += xIni;

			if( dato.valor < 0 )
			{
				y2 = ( positivoYnegativo ? halfHeight : halfHeight - 20 ) + Math.abs( pixVal );
			}
			else
			{
				y2 = ( positivoYnegativo ? halfHeight : halfHeight - 20 ) - pixVal;
			}

			if( ( y1 <= halfHeight ) && ( y2 <= halfHeight ) ) // Ambos por Encima del eje
			{
				graphics2d.setColor( positive == null ? UtilidadesLineChart.Colors.GREEN : positive );
				graphics2d.setStroke( new BasicStroke( graphStroke ) );
				Line2D.Double linea = new Line2D.Double( x1, y1, x2, y2 );
				graphics2d.draw( linea );
			}
			else if( ( y1 >= halfHeight ) && ( y2 >= halfHeight ) ) // Ambos por Debajo del eje
			{
				graphics2d.setColor( negative == null ? UtilidadesLineChart.Colors.PINK : negative );
				graphics2d.setStroke( new BasicStroke( graphStroke ) );
				Line2D.Double linea = new Line2D.Double( x1, y1, x2, y2 );
				graphics2d.draw( linea );
			}
			else if( ( y1 < halfHeight ) && ( y2 > halfHeight ) ) // 1: Por encima. 2: Por debajo
			{
				double y1t = ( halfHeight - y1 );
				double y2t = ( halfHeight - y2 );

				double xEn0 = darXdeYenRecta( x1, y1t, x2, y2t, 0 );
				double yEn0 = halfHeight;

				graphics2d.setColor( positive == null ? UtilidadesLineChart.Colors.GREEN : positive );
				graphics2d.setStroke( new BasicStroke( graphStroke ) );

				Line2D.Double linea0 = new Line2D.Double( x1, y1, xEn0, yEn0 );
				graphics2d.draw( linea0 );

				graphics2d.setColor( negative == null ? UtilidadesLineChart.Colors.PINK : negative );
				graphics2d.setStroke( new BasicStroke( graphStroke ) );

				Line2D.Double linea1 = new Line2D.Double( xEn0, yEn0, x2, y2 );
				graphics2d.draw( linea1 );
			}
			else if( ( y1 > halfHeight ) && ( y2 < halfHeight ) ) // 1: Por debajo. 2: Por encima
			{
				double y1t = ( halfHeight - y1 );
				double y2t = ( halfHeight - y2 );

				double xEn0 = darXdeYenRecta( x1, y1t, x2, y2t, 0 );
				double yEn0 = halfHeight;

				graphics2d.setColor( negative == null ? UtilidadesLineChart.Colors.PINK : negative );
				graphics2d.setStroke( new BasicStroke( graphStroke ) );

				Line2D.Double linea0 = new Line2D.Double( x1, y1, xEn0, yEn0 );
				graphics2d.draw( linea0 );

				graphics2d.setColor( positive == null ? UtilidadesLineChart.Colors.GREEN : positive );
				graphics2d.setStroke( new BasicStroke( graphStroke ) );

				Line2D.Double linea1 = new Line2D.Double( xEn0, yEn0, x2, y2 );
				graphics2d.draw( linea1 );
			}
			else // DEFAULT - ERROR
			{
				graphics2d.setColor( UtilidadesLineChart.Colors.AZUL_ED );
				graphics2d.setStroke( new BasicStroke( graphStroke ) );
				Line2D.Double linea = new Line2D.Double( x1, y1, x2, y2 );
				graphics2d.draw( linea );
			}
			pintarPuntosY( graphics2d, y2, dato.valor );
			pintarPuntosX( graphics2d, halfHeight, x2, dato.textX != null ? dato.textX : String.valueOf( contador ) );
			pintarDot( graphics2d, x2, y2, dato );

			lastPoint = new Point( ( int ) x2, ( int ) y2 );
		}
	}

	/**
	 * Pinta los puntos de interés en la gráfica
	 * 
	 * @param graphics2d Gráfica en la que se pintará
	 * @param x Coordenada en X del punto
	 * @param y Coordenada en Y del punto
	 * @param dato Dato correspondiente al punto
	 */
	private void pintarDot( Graphics2D graphics2d, double x, double y, Dato dato )
	{
		Color bg = dato.valor >= 0 ? positive == null ? UtilidadesLineChart.Colors.GREEN : positive : negative == null ? UtilidadesLineChart.Colors.PINK : negative;
		Color fg = dato.valor >= 0 ? background == null ? UtilidadesLineChart.Colors.GRIS : background : foreground == null ? UtilidadesLineChart.Colors.BLANCO : foreground;

		graphics2d.setColor( bg );
		Ellipse2D.Double dot = new Ellipse2D.Double( x - 5, y - 5, 10, 10 );
		DotMouseMotionListener dotListener = new DotMouseMotionListener( dot, ( muestraNombrePopup ? "<html><center>" + dato.textX + "<br>" : "" ) + ( formatoMoneda ? UtilidadesLineChart.darFormatoNúmeroValor( String.valueOf( dato.valor ) ) : String.valueOf( dato.valor ) ) );
		dotListener.setColors( bg, fg );
		listeners.add( dotListener );
		addMouseMotionListener( dotListener );
		graphics2d.fill( dot );
	}

	/**
	 * Pinta los ejes de la gráfica
	 * 
	 * @param graphics2d Gráfica en la que se pintará
	 * @param width Ancho
	 * @param heightTotal Altura total
	 * @param heightXAxis Altura del eje X
	 */
	private void pintarEjes( Graphics2D graphics2d, final double width, final double heightTotal, final double heightXAxis )
	{
		graphics2d.setColor( ( background == null ? UtilidadesLineChart.Colors.GRIS : background ).brighter( ).brighter( ).brighter( ) );
		graphics2d.setStroke( new BasicStroke( 2.5f ) );

		double x1ey = xIni;
		double y1ey = yIni;
		double x2ey = x1ey;
		double y2ey = heightTotal;
		Line2D.Double ejeY = new Line2D.Double( x1ey, y1ey, x2ey, y2ey );
		graphics2d.draw( ejeY );

		graphics2d.setColor( ( background == null ? UtilidadesLineChart.Colors.GRIS : background ).brighter( ).brighter( ).brighter( ) );
		graphics2d.setStroke( new BasicStroke( 2f ) );

		double x1ex = xIni;
		double y1ex = heightXAxis;
		double x2ex = width + xIni;
		double y2ex = y1ex;

		Line2D.Double ejeX = new Line2D.Double( x1ex, y1ex, x2ex, y2ex );
		graphics2d.draw( ejeX );
	}

	/**
	 * Pinta la linea de promedio de los datos
	 * 
	 * @param graphics2d Gráfica en la que se pintará
	 * @param width Ancho del panel
	 * @param height Alto del panel
	 * @param maxValue Valor máximo de la gráfica
	 */
	private void pintarPromedio( Graphics2D graphics2d, final double width, final double height, double maxValue )
	{
		double prom = 0;
		for( Dato dato : datos )
		{
			prom += dato.valor;
		}
		if( datos.size( ) != 0 )
		{
			prom /= datos.size( );
		}

		double halfHeight = positivoYnegativo ? height / 2 : height;

		double x1 = xIni;
		double x2 = width + xIni;

		double pixVal = ( ( halfHeight - ( yIni * 2 ) ) * prom ) / maxValue;
		double y1 = halfHeight - pixVal - 2f;
		double y2 = 4;

		Color color = ( background == null ? UtilidadesLineChart.Colors.GRIS : background ).darker( );
		graphics2d.setColor( color );
		Rectangle2D.Double rectangle = new Rectangle2D.Double( x1, y1 - 2, x2 - x1, y2 );

		Color bg = color;
		Color fg = Colors.BLANCO;

		DotMouseMotionListener dotMouseMotionListener = new DotMouseMotionListener( rectangle, "<html><center>Promedio <br>" + ( formatoMoneda ? UtilidadesLineChart.darFormatoNúmeroValor( String.valueOf( prom ) ) : String.valueOf( UtilidadesLineChart.round( prom, 2 ) ) ) );
		dotMouseMotionListener.setColors( bg, fg );
		listeners.add( dotMouseMotionListener );
		addMouseMotionListener( dotMouseMotionListener );

		graphics2d.fill( rectangle );

		pintarPuntosY( graphics2d, y1, UtilidadesLineChart.round( prom, 2 ), color.darker( ).darker( ) );
	}

	/**
	 * Pinta los puntos en el eje X
	 * 
	 * @param graphics2d Gráfica en la cual se pintarán los datos
	 * @param xAxis Eje X de la gráfica
	 * @param x Coordenada en X
	 * @param nombre Nombre del dato
	 */
	private void pintarPuntosX( Graphics2D graphics2d, double xAxis, double x, String nombre )
	{
		double x1 = x;
		double y1 = positivoYnegativo ? xAxis - 5 : xAxis - 20;
		double x2 = x;
		double y2 = positivoYnegativo ? xAxis + 5 : xAxis - 10;

		graphics2d.setStroke( new BasicStroke( 1 ) );
		Line2D.Double linea = new Line2D.Double( x1, y1, x2, y2 );
		graphics2d.draw( linea );

		FontMetrics metrics = graphics2d.getFontMetrics( getFont( ) );
		int hString = ( metrics.getAscent( ) - metrics.getDescent( ) ) / 2;
		hString = metrics.getHeight( );
		int wString = metrics.stringWidth( nombre );

		graphics2d.setColor( foreground == null ? UtilidadesLineChart.Colors.BLANCO : foreground );
		graphics2d.drawString( nombre, ( int ) x1 - ( wString / 2 ), ( int ) y2 + hString );
	}

	/**
	 * Pinta los puntos en el eje y
	 * 
	 * @param graphics2d Gráfica en la cual se pintarán los datos
	 * @param y Coordenada en Y
	 * @param real Valor del dato
	 */
	private void pintarPuntosY( Graphics2D graphics2d, double y, final double real )
	{
		double x1 = xIni;
		double y1 = y;
		double x2 = Math.abs( xIni - 8 );
		double y2 = y;

		graphics2d.setStroke( new BasicStroke( 1 ) );
		if( real >= 0 )
		{
			graphics2d.setColor( positive == null ? UtilidadesLineChart.Colors.GREEN : positive );
		}
		else
		{
			graphics2d.setColor( negative == null ? UtilidadesLineChart.Colors.PINK : negative );
		}

		Line2D.Double linea = new Line2D.Double( x1, y1, x2, y2 );
		graphics2d.draw( linea );

		FontMetrics metrics = graphics2d.getFontMetrics( getFont( ) );
		int hString = ( metrics.getAscent( ) - metrics.getDescent( ) ) / 2;
		int wString = metrics.stringWidth( String.valueOf( real ) );

		String texto = String.valueOf( real );
		texto = formatoMoneda ? UtilidadesLineChart.darFormatoNúmeroValor( texto ) : texto;

		graphics2d.drawString( texto, ( int ) x2 - wString, ( int ) y + hString );
	}

	/**
	 * Pinta los puntos en el eje y
	 * 
	 * @param graphics2d Gráfica en la cual se pintarán los datos
	 * @param y Coordenada en Y
	 * @param real Valor del Dato
	 * @param color Color del Dato
	 */
	private void pintarPuntosY( Graphics2D graphics2d, double y, final double real, Color color )
	{
		double x1 = xIni;
		double y1 = y;
		double x2 = Math.abs( xIni - 8 );
		double y2 = y;

		graphics2d.setStroke( new BasicStroke( 1 ) );

		graphics2d.setColor( color );

		Line2D.Double linea = new Line2D.Double( x1, y1, x2, y2 );
		graphics2d.draw( linea );

		FontMetrics metrics = graphics2d.getFontMetrics( getFont( ) );
		int hString = ( metrics.getAscent( ) - metrics.getDescent( ) ) / 2;
		int wString = metrics.stringWidth( String.valueOf( real ) );

		String texto = String.valueOf( real );
		texto = formatoMoneda ? UtilidadesLineChart.darFormatoNúmeroValor( texto ) : texto;

		graphics2d.drawString( texto, ( int ) x2 - wString, ( int ) y + hString );
	}

	/**
	 * Remueve todos los listeners
	 */
	private void removeListeners( )
	{
		for( DotMouseMotionListener dotMouseMotionListener : listeners )
		{
			removeMouseMotionListener( dotMouseMotionListener );
		}
	}

	/**
	 * Configura los colores Positivo, Negativo, Background, Foreground
	 * 
	 * @param pos Color de valores positivos
	 * @param neg Color de valores negativos
	 * @param bg Color del background
	 * @param fg Color del foreground
	 */
	public void setColors( Color pos, Color neg, Color bg, Color fg )
	{
		positive = pos;
		negative = neg;
		background = bg;
		foreground = fg;

		setBackground( bg );
	}

	/**
	 * Define si se pinta la linea de promedio
	 * 
	 * @param lineaPromedio True: Se pinta la linea. False: No se pinta la linea
	 */
	public void setLineaPromedio( boolean lineaPromedio )
	{
		this.lineaPromedio = lineaPromedio;
	}

	/**
	 * Define si se muestra el nombre del valor en el Popup
	 * 
	 * @param muestraNombrePopup True si se muestra el nombre, False de lo contrario
	 */
	public void setMuestraNombrePopup( boolean muestraNombrePopup )
	{
		this.muestraNombrePopup = muestraNombrePopup;
	}

	/**
	 * Define si se muestra los cuadrantes positivos y negativos de la gráfica
	 * 
	 * @param positivoYnegativo True si se muestran ambos cuadrantes, False de lo contrario
	 */
	public void setPositivoYnegativo( boolean positivoYnegativo )
	{
		this.positivoYnegativo = positivoYnegativo;
	}

	/**
	 * Test
	 * 
	 * @param args
	 */
	public static void main( String[ ] args )
	{
		JFrame frame = new JFrame( );

		LineChart lineChart = new LineChart( );
		lineChart.agregarDato( 5000, "Dato1" );
		lineChart.agregarDato( 100000, "Dato2" );
		lineChart.agregarDato( 30000, "Dato3" );
		lineChart.agregarDato( 00, "Dato4" );
		lineChart.agregarDato( 80000, "Dato5" );
		// lineChart.agregarDato( -8000, "Dato6" );
		lineChart.ejeYFormatoMoneda( true );
		lineChart.setPositivoYnegativo( !true );
		lineChart.setColors( Colors.BLANCO, Colors.BLANCO, Colors.AZUL_ED, Colors.BLANCO );
		lineChart.setLineaPromedio( true );
		lineChart.muestraNombrePopup = !true;

		frame.add( lineChart );

		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.pack( );
		frame.setLocationRelativeTo( null );
		frame.setVisible( true );
	}
}