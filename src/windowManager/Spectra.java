package windowManager;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Spectra {

	public String name;
	public File path;
	public String datetime;
	public String monitor;
	public String LT;
	public int[] values = new int[128];
	public int edge=-1;
	public double ADC1; public double ADC1StD; public double ADC2; public double ADC2StD; public double ADC3; public double ADC3StD;
	public double T1; public double T1StD; public double T2; public double T2StD; public double T3; public double T3StD;
	public double counter1; public double counter2;
	public int RNIntegral = 0;
	//bottom of the file
	public double fluxslope;public double fluxoffset;public double ADC2Slope;public double ADC2Offset;public double ADC3Slope;public double ADC3Offset;public double Temp1Slope;public double Temp1Offset;
	public double Temp2Slope;public double Temp2Offset;public double Temp3Slope;public double Temp3Offset;public double Counter1Slope;public double Counter1Offset;public double Counter2Slope;public double Counter2Offset;

    public Spectra(String _name, File _path) throws Exception  {
    	path = _path;
    	name = _name;
    	//read spectra line by line
        FileReader fileReader;
		try {
			fileReader = new FileReader(path);
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        List<String> lines = new ArrayList<String>();
	        String line = null;
	        while ((line = bufferedReader.readLine()) != null) {
	            lines.add(line);
	        }
	        bufferedReader.close();
	        System.out.println("read in spectra with " + lines.size() + "lines");
	        try {
	        	monitor = lines.get(131);
	        } catch (Exception e) {
	        	System.out.print(lines);
	        	return;
	        }
	        LT = lines.get(2);
	        //get only the LT as a number (split after first space)
	        LT= LT.substring(0, LT.indexOf(" "));
	        datetime = lines.get(0);
	        if(lines.size()==136) {
	        	if(!lines.get(135).isEmpty())
	        		edge = Integer.parseInt(lines.get(135));
	        }
	        //fill values
	        for(int i=3; i<131; i++) {
	        	values[i-3] = Integer.parseInt(lines.get(i));
	        	RNIntegral += values[i-3];
	        }
	        //get temperature etc.
	        // String[] lines.get(1).split(";") = lines.get(1).split(";");
	        //get only the LT as a number (split after first space)
	        ADC1= Double.parseDouble(lines.get(1).split(";")[0]);
	        ADC2= Double.parseDouble(lines.get(1).split(";")[2]);
	        ADC3= Double.parseDouble(lines.get(1).split(";")[4]);
	        ADC1StD= Double.parseDouble(lines.get(1).split(";")[1]);
	        ADC2StD= Double.parseDouble(lines.get(1).split(";")[3]);
	        ADC3StD= Double.parseDouble(lines.get(1).split(";")[5]);
	        T1= Double.parseDouble(lines.get(1).split(";")[6]);
	        T2= Double.parseDouble(lines.get(1).split(";")[8]);
	        T3= Double.parseDouble(lines.get(1).split(";")[10]);
	        T1StD= Double.parseDouble(lines.get(1).split(";")[7]);
	        T2StD= Double.parseDouble(lines.get(1).split(";")[9]);
	        T3StD= Double.parseDouble(lines.get(1).split(";")[11]);
	        //nicht sicher obs wirklich die counter sind
	        counter1= Double.parseDouble(lines.get(1).split(";")[12]);
	        counter2= Double.parseDouble(lines.get(1).split(";")[14]);
	        //bottom part of data        
	        fluxslope=Double.parseDouble(lines.get(132).split(";")[0]);
	        fluxoffset=Double.parseDouble(lines.get(132).split(";")[1]);
	        ADC2Slope=Double.parseDouble(lines.get(132).split(";")[2]);
	        ADC2Offset=Double.parseDouble(lines.get(132).split(";")[3]);
	        ADC3Slope=Double.parseDouble(lines.get(132).split(";")[4]);
	        ADC3Offset=Double.parseDouble(lines.get(132).split(";")[5]);
	        Temp1Slope=Double.parseDouble(lines.get(133).split(";")[0]);
	        Temp1Offset=Double.parseDouble(lines.get(133).split(";")[1]);
	        Temp2Slope=Double.parseDouble(lines.get(133).split(";")[2]);
	        Temp2Offset=Double.parseDouble(lines.get(133).split(";")[3]);
	        Temp3Slope=Double.parseDouble(lines.get(133).split(";")[4]);
	        Temp3Offset=Double.parseDouble(lines.get(133).split(";")[5]);
	        Counter1Slope=Double.parseDouble(lines.get(134).split(";")[0]);
	        Counter1Offset=Double.parseDouble(lines.get(134).split(";")[1]);
	        Counter2Slope=Double.parseDouble(lines.get(134).split(";")[2]);
	        Counter2Offset=Double.parseDouble(lines.get(134).split(";")[3]);
	        System.out.println("Read in" + name);
		} catch (IOException e) {
	        System.out.println("Could not read in Spectra"); 
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			//e.g. out pof bounds -> broken spectrum?
			System.out.println("Broken Spectrum");
			e.printStackTrace();
		}
      }
    
    //constructor for the hardcoded reference spectrum
    public Spectra() {
    	//hard coded reference spectrum copied from the old delphi RnLog
    	//casted to int and *10 for more precise integers
    	this.values[0] = (int) ( 0.0 *10);
    	this.values[1] = (int) ( 0.0 *10);
        this.values[2] = (int) ( 0.0 *10);
        this.values[3] = (int) ( 0.0 *10);
        this.values[4] = (int) ( 0.0 *10);
        this.values[5] = (int) ( 0.0 *10);
        this.values[6] = (int) ( 0.0 *10);
        this.values[7] = (int) ( 0.0 *10);
        this.values[8] = (int) ( 0.0 *10);
        this.values[9] = (int) ( 0.0 *10);
        this.values[10] = (int) ( 0.0 *10);
        this.values[11] = (int) ( 0.0 *10);
        this.values[12] = (int) ( 0.0 *10);
        this.values[13] = (int) ( 0.0 *10);
        this.values[14] = (int) ( 0.0 *10);
        this.values[15] = (int) ( 23.692 *10);
        this.values[16] = (int) ( 24.077 *10);
        this.values[17] = (int) ( 24.0 *10);
        this.values[18] = (int) ( 25.231 *10);
        this.values[19] = (int) ( 21.308 *10);
        this.values[20] = (int) ( 22.308 *10);
        this.values[21] = (int) ( 19.692 *10);
        this.values[22] = (int) ( 22.769 *10);
        this.values[23] = (int) ( 25.769 *10);
        this.values[24] = (int) ( 24.846 *10);
        this.values[25] = (int) ( 27.077 *10);
        this.values[26] = (int) ( 28.385 *10);
        this.values[27] = (int) ( 32.231 *10);
        this.values[28] = (int) ( 31.538 *10);
        this.values[29] = (int) ( 33.538 *10);
        this.values[30] = (int) ( 35.692 *10);
        this.values[31] = (int) ( 33.615 *10);
        this.values[32] = (int) ( 36.0 *10);
        this.values[33] = (int) ( 38.615 *10);
        this.values[34] = (int) ( 43.846 *10);
        this.values[35] = (int) ( 43.846 *10);
        this.values[36] = (int) ( 43.077 *10);
        this.values[37] = (int) ( 47.077 *10);
        this.values[38] = (int) ( 50.846 *10);
        this.values[39] = (int) ( 54.231 *10);
        this.values[40] = (int) ( 59.077 *10);
        this.values[41] = (int) ( 55.461 *10);
        this.values[42] = (int) ( 59.923 *10);
        this.values[43] = (int) ( 64.769 *10);
        this.values[44] = (int) ( 68.0 *10);
        this.values[45] = (int) ( 69.538 *10);
        this.values[46] = (int) ( 71.692 *10);
        this.values[47] = (int) ( 75.384 *10);
        this.values[48] = (int) ( 81.769 *10);
        this.values[49] = (int) ( 78.385 *10);
        this.values[50] = (int) ( 92.154 *10);
        this.values[51] = (int) ( 100.46 *10);
        this.values[52] = (int) ( 100.62 *10);
        this.values[53] = (int) ( 104.77 *10);
        this.values[54] = (int) ( 110.92 *10);
        this.values[55] = (int) ( 117.15 *10);
        this.values[56] = (int) ( 121.77 *10);
        this.values[57] = (int) ( 126.154 *10);
        this.values[58] = (int) ( 136.46 *10);
        this.values[59] = (int) ( 141.62 *10);
        this.values[60] = (int) ( 152.85 *10);
        this.values[61] = (int) ( 156.08 *10);
        this.values[62] = (int) ( 157.77 *10);
        this.values[63] = (int) ( 157.77 *10);
        this.values[64] = (int) ( 179.54 *10);
        this.values[65] = (int) ( 194.46 *10);
        this.values[66] = (int) ( 181.08 *10);
        this.values[67] = (int) ( 199.38 *10);
        this.values[68] = (int) ( 205.69 *10);
        this.values[69] = (int) ( 212.85 *10);
        this.values[70] = (int) ( 202.85 *10);
        this.values[71] = (int) ( 204.31 *10);
        this.values[72] = (int) ( 216.62 *10);
        this.values[73] = (int) ( 227.69 *10);
        this.values[74] = (int) ( 242.00 *10);
        this.values[75] = (int) ( 260.77 *10);
        this.values[76] = (int) ( 266.15 *10);
        this.values[77] = (int) ( 288.00 *10);
        this.values[78] = (int) ( 304.31 *10);
        this.values[79] = (int) ( 329.69 *10);
        this.values[80] = (int) ( 350.15 *10);
        this.values[81] = (int) ( 368.23 *10);
        this.values[82] = (int) ( 390.15 *10);
        this.values[83] = (int) ( 410.15 *10);
        this.values[84] = (int) ( 405.46 *10);
        this.values[85] = (int) ( 434.77 *10);
        this.values[86] = (int) ( 447.00 *10);
        this.values[87] = (int) ( 460.69 *10);
        this.values[88] = (int) ( 463.08 *10);
        this.values[89] = (int) ( 464.23 *10);
        this.values[90] = (int) ( 443.38 *10);
        this.values[91] = (int) ( 408.46 *10);
        this.values[92] = (int) ( 363.00 *10);
        this.values[93] = (int) ( 298.92 *10);
        this.values[94] = (int) ( 179.62 *10);
        this.values[95] = (int) ( 64.538 *10);
        this.values[96] = (int) ( 42.154 *10);
        this.values[97] = (int) ( 43.385 *10);
        this.values[98] = (int) ( 44.923 *10);
        this.values[99] = (int) ( 48.462 *10);
        this.values[100] = (int) ( 48.000 *10);
        this.values[101] = (int) ( 54.615 *10);
        this.values[102] = (int) ( 54.769 *10);
        this.values[103] = (int) ( 57.846 *10);
        this.values[104] = (int) ( 57.154 *10);
        this.values[105] = (int) ( 59.846 *10);
        this.values[106] = (int) ( 55.692 *10);
        this.values[107] = (int) ( 59.461 *10);
        this.values[108] = (int) ( 46.154 *10);
        this.values[109] = (int) ( 41.231 *10);
        this.values[110] = (int) ( 28.462 *10);
        this.values[111] = (int) ( 15.538 *10);
        this.values[112] = (int) ( 16.461 *10);
        this.values[113] = (int) ( 8.1538 *10);
        this.values[114] = (int) ( 6.2308 *10);
        this.values[115] = (int) ( 4.2308 *10);
        this.values[116] = (int) ( 3.0769 *10);
        this.values[117] = (int) ( 1.6154 *10);
        this.values[118] = (int) ( 1.0769 *10);
        this.values[119] = (int) ( 0.84615 *10);
        this.values[120] = (int) ( 1.2308 *10);
        this.values[121] = (int) ( 0.84615 *10);
        this.values[122] = (int) ( 0.15385 *10);
        this.values[123] = (int) ( 0.23077 *10);
        this.values[124] = (int) ( 0.076923 *10);
        this.values[125] = (int) ( 0.15385 *10);
        this.values[126] = (int) ( 0.076923 *10);
        this.values[127] = (int) ( 1.3846 *10);
        this.name="temp_ref_spec";
        this.path = null;
    }
    
    //constructor for live spectrum
    public Spectra(String[] specLines) {
    	//gets a String array containing the lines of the spectra
        LT = "0";
        monitor = specLines[130];
        //fill values
        for(int i=1; i<131; i++) {
        	values[i-1] = Integer.parseInt(specLines[i]);
        }
        //get temperature etc.
        // String[] specLines[1).split(";") = specLines[1).split(";");
        //get only the LT as a number (split after first space)
        ADC1= Double.parseDouble(specLines[0].split(" ")[2]);
        ADC2= Double.parseDouble(specLines[0].split(" ")[3]);
        ADC3= Double.parseDouble(specLines[0].split(" ")[4]);
        ADC1StD= 0.0;
        ADC2StD= 0.0;
        ADC3StD= 0.0;
        T1= Double.parseDouble(specLines[0].split(" ")[5]);
        T2= Double.parseDouble(specLines[0].split(" ")[6]);
        T3= Double.parseDouble(specLines[0].split(" ")[7]);
        T1StD= 0.0;
        T2StD= 0.0;
        T3StD= 0.0;
        //nicht sicher obs wirklich die counter sind
        counter1= Double.parseDouble(specLines[0].split(" ")[8]);
        counter2= Double.parseDouble(specLines[0].split(" ")[9]);    
        RNIntegral = Integer.parseInt(specLines[0].split(" ")[10]);
        fluxslope=0.0;
        fluxoffset=0.0;
        ADC2Slope=0.0;
        ADC2Offset=0.0;
        ADC3Slope=0.0;
        ADC3Offset=0.0;
        Temp1Slope=0.0;
        Temp1Offset=0.0;
        Temp2Slope=0.0;
        Temp2Offset=0.0;
        Temp3Slope=0.0;
        Temp3Offset=0.0;
        Counter1Slope=0.0;
        Counter1Offset=0.0;
        Counter2Slope=0.0;
        Counter2Offset=0.0;
    	
    	
    	/*
    	04.07.2018 13:00:00
    	1093.943;3.725;0.000;0.000;0.000;0.000;0.000;0.410;0.000;0.410;0.000;0.410; 0.000;0.000; 0.000;0.000;
    	551 LT
    	14
    	19
    	24
    	15
    	9
    	2
    	2
    	5
    	2
    	2
    	0
    	0
    	0
    	0
    	4
    	3
    	16
    	10
    	7
    	13
    	16
    	31
    	18
    	36
    	31
    	37
    	43
    	51
    	33
    	64
    	50
    	65
    	89
    	104
    	103
    	100
    	77
    	48
    	28
    	21
    	21
    	5
    	11
    	8
    	5
    	2
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	0
    	4_KIT_30
    	19.85000; 0.00000; 1.00000; 0.00000; 1.00000; 0.00000;slope offset 3x ADC
    	 1.00000; 0.00000; 1.00000; 0.00000; 1.00000; 0.00000;slope offset 3x Temp
    	 1.00000; 0.00000; 1.00000; 0.00000;slope offset 2x Cnt
    	49*/

    	
    }
    
    //constructor for temporary reference spectrum
    public Spectra(ArrayList<Spectra> _spectraList) throws IOException {
    	System.out.println("create temporary reference spectrum");
    	name = "temp_ref_spec.ref";
    	//get ParentFile (directory) of first Spectra in List
    	path = new File(_spectraList.get(0).path.getParent()+ "\\temp_ref_spec.ref");
    	FileOutputStream fos = new FileOutputStream(path);
    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
    	System.out.println("Created temporary reference Spectrum at :" + path);
    	name = "temporary reference spectrum";
    	bw.write(name + "\r\n");
    	datetime = _spectraList.get(0).datetime;
    	bw.write(datetime + "\r\n");
    	ADC1 = _spectraList.get(0).ADC1;			ADC1StD = _spectraList.get(0).ADC1StD;
		ADC2 = _spectraList.get(0).ADC2; 			ADC2StD = _spectraList.get(0).ADC2StD;
		ADC2 = _spectraList.get(0).ADC3; 			ADC3StD = _spectraList.get(0).ADC3StD;
		T1 = _spectraList.get(0).T1; 				T1StD = _spectraList.get(0).T1StD;
		T2 = _spectraList.get(0).T2; 				T2StD = _spectraList.get(0).T2StD;
		T3 = _spectraList.get(0).T3; 				T3StD = _spectraList.get(0).T3StD;
		counter1 = _spectraList.get(0).counter1; 	counter2 = _spectraList.get(0).counter2;
    	bw.write(
    			ADC1 + ";" + ADC1StD + ";" +
    			ADC2 + ";" + ADC2StD + ";" +
    			ADC3 + ";" + ADC3StD + ";" +
    			T1 + ";" + T1StD + ";" +
    			T2 + ";" + T2StD + ";" +
    			T3 + ";" + T3StD + ";" +
    			counter1 + ";" + counter2+  ";\r\n"
    			);
    	LT = _spectraList.get(0).LT;
    	bw.write(LT +" LT\r\n");
    	RNIntegral = 0;
    	//write the values as sums of all Spectra in the list
    	System.out.println("write added values");
    	for(int j=0; j<128; j++) {
    		//RNIntegral += _spectraList.get(j).RNIntegral;
        	int tmp = 0;
        	for(int i=0; i<_spectraList.size(); i++) {
        		tmp += _spectraList.get(i).values[j];
            }
        	values[j]=tmp;
        	bw.write(values[j] + "\r\n");
    	}
    	bw.write(_spectraList.get(0).monitor + "\r\n");
    	//TODO: hinzufügen vom rest der datei ()die werte am ende)
    	bw.write("- \r\n- \r\n");
    	bw.close();
	}

    
    public void removeEdge() throws IOException {
    	if(edge == -1) {
    		//no edge set
    		System.out.println("no Edge set");
    		return;		
    	}
    	edge = -1;
    	BufferedReader file = new BufferedReader(new FileReader(path));
        StringBuffer inputBuffer = new StringBuffer();
        for (int row = 0; row < 135; row++) {
            inputBuffer.append(file.readLine());
            inputBuffer.append("\r\n");
        }
        String inputStr = inputBuffer.toString();
        file.close();
        System.out.println("inputString \n" + inputStr); // check that it's inputted right
        // write the new String with the replaced line OVER the same file
        FileOutputStream fileOut = new FileOutputStream(path);
        fileOut.write(inputStr.getBytes());
        fileOut.close();
		System.out.println("removed Edge"); 
    }
    
	private void setEdge(int _edge) throws IOException {
		System.out.println("seting Edge");  
    	this.removeEdge();
		edge = _edge;
    	BufferedReader file = new BufferedReader(new FileReader(path));
        StringBuffer inputBuffer = new StringBuffer();
        for (int row = 0; row < 135; row++) {
            inputBuffer.append(file.readLine());
            inputBuffer.append("\r\n");
        }
        inputBuffer.append(edge);
        String inputStr = inputBuffer.toString();
        System.out.println("set Edge input Str"+ edge + "\n" + inputStr); 
        file.close();
        FileOutputStream fileOut = new FileOutputStream(path);
        fileOut.write(inputStr.getBytes());
        fileOut.close();
		System.out.println("set Edge of " + this.name + " to " + edge); 
	}
	
	public int kreuzProdukt(Spectra other, int shift) {
		//calculates the sum of the products of the energychannel values, 
		//Spectra1.canal[1] * Spectra2.canal[1+shift] + Spectra1.canal[2] * Spectra2.canal[2+shift] etc.
		//using modulu to prevent overflow
		//returns the sum
		int sum = 0;
		for(int i=1; i<128; i++) {
			//(((x)%128)+128)%128 necessary because java oes not use the modulu but the remainer, which means it produced negative numbers (out of bounds exception)
			sum += this.values[i]*other.values[(((i+shift)%128)+128)%128];
		}
		return sum;
	}
	
	public int[] calcKreuzKorrelation (Spectra other, int minDelta, int maxDelta) {
		//computes with kreuzProdukt() the correlations between two spectra 
		//for all shifts in range of minDelta to maxDelta
		//min and max Delta are determined in the .ini file as thres3 and thres4
		int[] kreuzKorrelationen = new int[256];
		for(int i=-minDelta; i<=maxDelta; i++) {
			kreuzKorrelationen[i+128] = this.kreuzProdukt(other, i);
		}
		return kreuzKorrelationen;
		//the maximum of this list indicates how much the edge has to be shifted to fit in
		//EdgeShift = Indes of max(kreuzrelationen) -128
	}
	
	
	
	public void calcEdge(Spectra reference, int minDelta, int maxDelta, int edgeoffset) throws IOException {
		//sets Edge of a spectrum according to edge in reference spectrum
		//using calcKreuzKorrelation
		System.out.println("calc edge of " + this.name);
		if(this.edge != -1) {
			//edge already set
			System.out.println("edge already set at " + this.edge);
			return;
		}
		int maxAt = 0;
		int [] kreuzKorrelationen = calcKreuzKorrelation(this, minDelta, maxDelta);
		for (int i = 0; i < kreuzKorrelationen.length; i++) {
		    maxAt = kreuzKorrelationen[i] > kreuzKorrelationen[maxAt] ? i : maxAt;
		}

		System.out.println("changed edge of "+ this.name + " at " + this.edge + " to " + ((maxAt-128)+edgeoffset));
		this.setEdge((maxAt-128)+edgeoffset);
		
	}
	
	public int integrate(int low, int up) {
		//returns the sum of values in the energychannels between to thresholds
		//for calculating RN1-3
		int result = 0;
		if(up<=low) {
			JOptionPane.showMessageDialog(null, "Lower noise threshold is higher than the window threshold. Please adjust them in the evaluation settings.", "Error creating .ext file", JOptionPane.INFORMATION_MESSAGE);
			return -1;
		}
		if(low<0 || up<0) {
			JOptionPane.showMessageDialog(null, "Lower noise threshold and window threshold are negative. Please adjust them in the evaluation settings.", "Error creating .ext file", JOptionPane.INFORMATION_MESSAGE);
			return -1;
		}
		if(low==0)low=1;
		for(int i = low-1; i<up; i++) {
			result += this.values[i];
		}
		return result;
	}
	
	public boolean checkEdge(Spectra actSpectrum, int edge, int lowerThres, int upperThres) {
		if(actSpectrum.edge >(edge-upperThres) && actSpectrum.edge<(edge+upperThres)) {
			return true;
		}
		return false;
	}
}
