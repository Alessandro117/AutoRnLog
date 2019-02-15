package windowManager;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ComputationThread implements Runnable {
	
	public JProgressBar progressBar;
	public JLabel lblProgress1;
	public JLabel lblProgress2;
	public Spectra RefSpec;
	public static String SoftwareVersion = "AutoRnLog 2.0";
	
	public ComputationThread(JLabel _lblProgress1, JLabel _lblProgress2, JProgressBar _progressBar) {
		progressBar = _progressBar;
		lblProgress1 = _lblProgress1;
		lblProgress2 = _lblProgress2;

		System.out.println("opening thread");
		
	}


	public void run(){
		iniFile ini = new iniFile();
		String lvl0=  ini.lvl0; //"C:\\Users\\Alessandro\\eclipse-workspace\\AutoRnLog\\lvl0\\";
		String lvl1=  ini.lvl1; //"C:\\Users\\Alessandro\\eclipse-workspace\\AutoRnLog\\lvl1\\";
		System.out.println("lvl0 directory: " + ini.lvl0);
		System.out.println("lvl1 directory: " + ini.lvl1);
		
		/*
		 * For getting all files on the card
		 * Has problems, if the spektrum on the SD card is broken, because
		 * telnetTunnel.java expects a fixed file length/amount of lines
		 */
		/*
		lblProgress1.setText("Getting Filenames from " + ini.IP);
		
		//for testing
		//ArrayList<String> filenames = new ArrayList<String>(); // getSpectrafromCard("129.206.22.122");
		ArrayList<String> filenames = getSpectrafromCard(ini.IP);
		lblProgress1.setText("Copying files from the SD Card");
		//for testing
		//filenames.add("10912001.R19"); filenames.add("10912301.R19");filenames.add("10913001.R19");filenames.add("10913301.R19");filenames.add("10914001.R19");filenames.add("10914301.R19");filenames.add("10915001.R19");
		double progress = 50/filenames.size();
		ArrayList<String> files = new ArrayList<String>();
		////////////////////////////////////////
		//getting the filenames and copying them
		////////////////////////////////////////

		telnetTunnel con = new telnetTunnel(ini.IP, 23);
		String spectra = null;
		try {
			TimeUnit.MILLISECONDS.sleep(1000);	
			files = con.snd(filenames, progressBar, lblProgress2);
		} catch (Exception e) {
			// Try once again
			e.printStackTrace();
			try {
				System.out.println("next try ");
				//spectra = con.set("snd", filenames.get(i));
			} catch (Exception e2) {
				lblProgress1.setText("Could not retrieve Files from SD Card");
				lblProgress2.setForeground(Color.RED);
				lblProgress2.setText("");
				e2.printStackTrace();
				System.exit(1);
			}
		}
		//write spectra String into file
		for(int i=0; i<files.size();i++) {
			try {			
	            File newTextFile = new File(lvl0 + ((String)filenames.get(i).trim()));
	            FileWriter fw = new FileWriter(newTextFile);
	            fw.write(files.get(i));
	            fw.close();
            copyFile(newTextFile, new File(lvl1+((String)filenames.get(i).trim())));
			} catch (IOException iox) {
	        	lblProgress1.setText("Could not safe " + ((String)filenames.get(i).trim()));
				lblProgress2.setForeground(Color.RED);
				lblProgress2.setText("");
	            iox.printStackTrace();
				System.exit(1);
			}
			
		}
		*/
		
		///////////////////////////////////////
		//Compute Filenames to get from monitor
		//amount of filenames = ini.amountOfSpectra
		///////////////////////////////////////
		
		ArrayList<String> getTheseFilenames = new ArrayList<String>();
		
		for(int a=0; a<Integer.valueOf(ini.amountOfSpectra); a++) {
			//get the current time, minuts half an hour times a
			//if u only want to get the last spectra, a is maximum zero -> only time.now is taken
			LocalDateTime now = LocalDateTime.now().minusMinutes(30*a);
			
			String firstChar = ini.id.substring(0,1);
			DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");  
			String tmpMonth = month.format(now);
			//if month is 10, 11 or 12, translate to A,B and C
			if(tmpMonth.equals("10")) {
				tmpMonth = "A";
			} else if(tmpMonth.equals("11")) {
				tmpMonth = "B";
			} else if(tmpMonth.equals("12")) {
				tmpMonth = "C";
			} else {
				tmpMonth = tmpMonth.substring(1,2);
			}
				
			DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
			String tmpDay = day.format(now);
			
			DateTimeFormatter year = DateTimeFormatter.ofPattern("yy");  
			String tmpYear = year.format(now);
			
			DateTimeFormatter hour = DateTimeFormatter.ofPattern("HH");
			String tmpHour = hour.format(now);
			DateTimeFormatter minute = DateTimeFormatter.ofPattern("mm");
			String tmpMin = minute.format(now).substring(0, 1);
			//round down to the last 30 minutes
			//a bit ugly 
			if(tmpMin.equals("1") || tmpMin.equals("2"))
				tmpMin="0";
			if(tmpMin.equals("4") || tmpMin.equals("5"))
				tmpMin="3";
			String FilenameToGet = tmpMonth + tmpDay + tmpHour + tmpMin + "0"+  firstChar +".R" + tmpYear;
			System.out.println("get this file: " + FilenameToGet);
			getTheseFilenames.add(FilenameToGet);
		}
		
		if(getTheseFilenames.size()==1)
			lblProgress1.setText("Copying " + getTheseFilenames + " from "+ ini.IP);
		if(getTheseFilenames.size()>1)
			lblProgress1.setText("Copying the latest " + getTheseFilenames.size() + " spectra from "+ ini.IP);
		
		////////////////////////////////////////
		//getting the filenames and copying them
		////////////////////////////////////////
		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<String> files = new ArrayList<String>();
		filenames = getTheseFilenames; 
		telnetTunnel con = new telnetTunnel(ini.IP, 23);
		try {
			TimeUnit.MILLISECONDS.sleep(1000);	
			files = con.snd(filenames, progressBar, lblProgress2);
		} catch (Exception e) {
			// Try once again
			e.printStackTrace();
			try {
				//if something happened, try again after 10 seconds
				System.out.println("connect again ");
				TimeUnit.MILLISECONDS.sleep(10000);
				files = con.snd(filenames, progressBar, lblProgress2);
			} catch (Exception e2) {
				lblProgress1.setText("Could not retrieve Files from SD Card");
				lblProgress2.setForeground(Color.RED);
				lblProgress2.setText("");
				e2.printStackTrace();
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(1);
			}
		}
		

		
		//write spectra String into file
		//in case several file needs to be gathered -> for loop
		System.out.println("gathered files: " +files);
		for(int i=0; i<files.size();i++) {
			File lvl0Directory = new File(lvl0);
			File lvl1Directory = new File(lvl1);
			if(!lvl0Directory.exists()) {
				//create the lvl0 directory
				System.out.println(lvl0Directory.exists() + " exisitert nicht");
				lvl0Directory.mkdirs();
			}
			System.out.println(lvl0Directory.getName() + lvl0Directory.getPath() + " exisitert");
			if(!lvl1Directory.exists()) {
				//create the lvl0 directory
				System.out.println(lvl1Directory.exists() + " exisitert nicht");
				lvl1Directory.mkdirs();
			}
			try {			
	            File newTextFile = new File(lvl0 + ((String)filenames.get(i).trim()));
	            FileWriter fw = new FileWriter(newTextFile);
	            fw.write(files.get(i));
	            fw.close();
	            copyFile(newTextFile, new File(lvl1+((String)filenames.get(i).trim())));
			} catch (IOException iox) {
	        	lblProgress1.setText("Could not safe " + ((String)filenames.get(i).trim()));
				lblProgress1.setForeground(Color.RED);
				lblProgress2.setText("");
	            iox.printStackTrace();
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(1);
			}
			
		}
		
		////////////////////////////////////////
		//get files in lvl1 directory and
		//create temp ref spectrum
		////////////////////////////////////////
		lblProgress1.setText("Creating temporary reference spectrum");
		lblProgress2.setText("");
		ArrayList<Spectra> spectraList = new ArrayList<Spectra>();
		//list all spectra
	    File folder = new File(lvl1);
	    File[] _files = folder.listFiles();
	    
	    //get the files in reverse order -> latest file first
	    Arrays.sort(_files, Collections.reverseOrder());
	    
	    //how many files should taken for extract/activity?
	    int count = 0; 
	    int max = Integer.parseInt(ini.evaluate);
		for (final File fileEntry : _files) {
			if(count>=max) {
				break;
			}
	        if (!fileEntry.isDirectory()) {
	        		try {
	        			//only add files with "R" on 10th place and digits/numbers on the last two places of the filename
	        			//->cheking if its a spectrum .R19
	        			if(fileEntry.getName().subSequence(9, 10).equals("R") && Character.isDigit(fileEntry.getName().substring(10).toCharArray()[0]) && Character.isDigit(fileEntry.getName().substring(10).toCharArray()[1])) {
	        				spectraList.add(new Spectra(fileEntry.getName(), fileEntry.getAbsoluteFile()));
	        				count++;
	        			}	
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}   	
	        }
		}
		
		//sort spectraList, so it becomes chronological for the extract file
		Collections.sort(spectraList, new Comparator<Spectra>() {
	        @Override
	        public int compare(Spectra spec1, Spectra spec2){
	            return  spec1.name.compareTo(spec2.name);
	        }
	    });
		//create reference spectrum
		try {
			RefSpec = new Spectra(spectraList);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//////////////////////////////////////
		//Extract logfile
		//////////////////////////////////////
		lblProgress1.setText("Creating Extract.txt");
		lblProgress2.setText("");
		String extFilename = "extract.txt";
		
		//extract spectra from spectra list and write it into extFile
		File extFile = null;
		System.out.println(lvl1 + "\\" + extFilename);
		extFile = new File(spectraList.get(0).path.getParent() + "\\" + extFilename);
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(extFile);
	    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOut));
	    	//write first line
	        bw.write("Date Time; Lifetime;ADC1; StdADC1; T1; StdT1;T2; StdT2;T3; StdT3;Rn1;Rn2;Rn3;Rn4;ADC2; StdADC2; ADC3; StdADC3; Counter1;Counter2;FluxSlope;FluxOffset;ADC2Slope;ADC2Offset;ADC3Slope;ADC3Offset;Temp1Slope;Temp1Offset;Temp2Slope;Temp2Offset;Temp3Slope;Temp3Offset;Counter1Slope;Counter1Offset;Counter2Slope;Counter2Offset;ID \r\n");
	        //loop though the selected spectra, see if they need to be flagged
	        int[] flaggedIdx = new int[spectraList.size()];
	        for(int i=0; i<spectraList.size(); i++) {
	        	
	        	//set edge of spectrum according to reference (if no edge is set yet)
	        	spectraList.get(i).calcEdge(RefSpec, ini.thres3, ini.thres4, ini.Edgeoffset);
	        	
	        	//flag spectra
	        	if(spectraList.get(i).edge > ini.Edgeoffset+ini.UpperFlagThres || spectraList.get(i).edge < ini.Edgeoffset-ini.LowerFlagThres) {
	        		//save Spectrum in new subfolder \flagged
	        		File tmpFlagged = new File( spectraList.get(i).path.getParent()+ "\\flagged\\" + spectraList.get(i).name);
	        		copyFile(spectraList.get(i).path, tmpFlagged);
	        		flaggedIdx[i]=1;
	        		System.out.println("copied " + spectraList.get(i).path.getPath() + " to " + tmpFlagged.getPath() );
	        		continue;
	        	}
	        	bw.write(spectraList.get(i).datetime + "; " +
	        	spectraList.get(i).LT + "; " +
	        	spectraList.get(i).ADC1 + "; "+
	        	spectraList.get(i).ADC1StD + "; "+
	        	spectraList.get(i).T1 + "; "+
	        	spectraList.get(i).T1StD + "; "+
	        	spectraList.get(i).T2 + "; "+
	        	spectraList.get(i).T2StD + "; "+
	        	spectraList.get(i).T3 + "; "+
	        	spectraList.get(i).T3StD + "; "+
	        	//RN1 in the old Delphi program, counts above the noise threshold (TotalThreshold)
	        	spectraList.get(i).integrate(ini.thres1, 128) + "; "+			
	        	//RN2 in the old Delphi, counts inside threshold window (between TotalThres and WindowThreshold)
	        	spectraList.get(i).integrate(ini.thres1, ini.thres2) + "; "+		
	        	//RN3 in the old Delphi, counts above the edge
	        	spectraList.get(i).integrate(spectraList.get(i).edge, 128)+ "; "+		
	        	//RN4 in the old Delphi
	        	spectraList.get(i).edge + "; "+		
	        	spectraList.get(i).ADC2 + "; "+
	        	spectraList.get(i).ADC2StD + "; "+
	        	spectraList.get(i).ADC3 + "; "+
	        	spectraList.get(i).ADC3StD + "; "+
	        	spectraList.get(i).counter1 + "; "+
	        	spectraList.get(i).counter2 + "; "+
	        	spectraList.get(i).fluxslope+ "; "+
	        	spectraList.get(i).fluxoffset+ "; "+
	        	spectraList.get(i).ADC2Slope+ "; "+
	        	spectraList.get(i).ADC2Offset+ "; "+
	        	spectraList.get(i).ADC3Slope+ "; "+
	        	spectraList.get(i).ADC3Offset+ "; "+
	        	spectraList.get(i).Temp1Slope+ "; "+
	        	spectraList.get(i).Temp1Offset+ "; "+
	        	spectraList.get(i).Temp2Slope+ "; "+
	        	spectraList.get(i).Temp2Offset+ "; "+
	        	spectraList.get(i).Temp3Slope+ "; "+
	        	spectraList.get(i).Temp3Offset+ "; "+
	        	spectraList.get(i).Counter1Slope+ "; "+
	        	spectraList.get(i).Counter1Offset+ "; "+
	        	spectraList.get(i).Counter2Slope+ "; "+
	        	spectraList.get(i).Counter2Offset+ "; "+
	        	spectraList.get(i).monitor+ "; \r\n"
	        	);
	        }
	        bw.close();
	        fileOut.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		///////////////////////////////////////////////
		//create activity file
		//////////////////////////////////////////////
        String actFilename = "activity.act";
		lblProgress1.setText("Creating activity.act");
		lblProgress2.setText("");
		String points = "1";
        //open ext and act file
		FileReader fileReader;
		FileOutputStream fileOut2;
		File actFile = new File(lvl1 + actFilename);
		try {
			//read in extract file
			fileReader = new FileReader(extFile);
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        ArrayList<String> extlines = new ArrayList<String>();
	        String line = null;
	        while ((line = bufferedReader.readLine()) != null) {
	            extlines.add(line);
	        }
	        bufferedReader.close();
	        System.out.println("successfully loaded extract file");
	        if(extlines.size()<2) {
	        	System.exit(1);
	        }
	        //open act File
			fileOut2 = new FileOutputStream(actFile);
	    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOut2));
	        bw.write("222-Radon activities calculated with " + SoftwareVersion + "\r\n"+ "\r\n");
	        bw.write("Evaluated by: " + SoftwareVersion + " on "); 
	        bw.write(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))); 
	        bw.write("\r\n");
	        bw.write("Used parameters \r\n");
	        String Method="Stockburger";
	        bw.write("Method	    : " + Method + "\r\n");
	        bw.write("Source File : " + extFile.getPath() + "\r\n");
	        bw.write("Solid Angle : " + String.valueOf(ini.solidangle) + "\r\n");
	        bw.write("Disequil.   : " + String.valueOf(ini.disequilibrium) + "\r\n");
	        bw.write("Flux Offset : " + String.valueOf(ini.fluxoffset) + "\r\n");
	        bw.write("Flux Slope  : " + String.valueOf(ini.fluxslope) + "\r\n"+"\r\n");	
	        bw.write("Format: \r\n");
	        bw.write("Stoptime,Activity [Bq/m3], Ac[dps],Ac/dt,Total, Window, Edge, temp1[C], temp2[C], temp3[C], Pressure[mbar], LifeTime[sec], Flux[m3/s], ID \r\n");
	        
	        //split extlines to get rid of duplicates or  missing values
	        ArrayList<ArrayList<String>> splittedExtlines = new ArrayList<ArrayList<String>>();
	        ArrayList<ArrayList<String>> splittedActlines = new ArrayList<ArrayList<String>>();
	        ArrayList<String> tmpList = new ArrayList<String>();

	        //save positions where to split
	        // 0-> dont split; 1-> split; 2-> delete 
	        int[] flag = new int[extlines.size()];
	        flag[1] = 0;
	        DateFormat formatter = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
	        
	        for (int i = 2; i< extlines.size(); i++) {
	        	flag[i] = 0;
	        	long last = formatter.parse(extlines.get(i-1).split(";")[0]).getTime();
	        	long actual =  formatter.parse(extlines.get(i).split(";")[0]).getTime();
	        	if((actual - last) > 1800000 ) {
	        		//if (Datetime_last - Datetime_current) > 1800s
	        		flag[i] = 1; //split here
	        		System.out.println("split " + (actual - last));
	        	}
	        	if((actual - last) < 60000 ) {
	        		//if (Datetime_last - Datetime_current) > 1800s
	        		flag[i] = 2; //remove this
	        		System.out.println("remove " + (actual - last));
	        	}
	        }
	        int j = 0;
	        for (int i = 1; i< extlines.size(); i++) {
	        	if (flag[i] == 0) {
	        		tmpList.add(extlines.get(i));
	        		System.out.println("add " + extlines.get(i));
	        	}
	        	if ( flag[i] == 1) {
	        		splittedExtlines.add((ArrayList<String>) tmpList.clone());
	        		tmpList.clear();
	        		j++;
	        		tmpList.add(extlines.get(i));
	        		System.out.println("new Array of extLines " + extlines.get(i));
	        	}
	        	if ( flag[i] == 2) {
	        		System.out.println("don't count this line (maybe duplicate) " + extlines.get(i));
	        		continue;
	        	}
	        }
	        splittedExtlines.add((ArrayList<String>) tmpList.clone());
    		tmpList.clear();
    		
    		//////////////////////////////////////////////////
	        //calculating the values via stockburger algorithm
    		//////////////////////////////////////////////////
	        System.out.println("Calculating Stockburger");
	        for(int x = 0; x < splittedExtlines.size(); x++) {	        	
	        	splittedActlines.add((ArrayList<String>) calcStockburger(splittedExtlines.get(x), Integer.parseInt(points)).clone());
	        }
	        
	        //gather and fuse, if fill == true
			//should the values be filled up?
	        Boolean fill = false;
	        if(ini.fill == 1) fill = true;
	        if(!fill || splittedActlines.size() == 1) {
	        	//just write the results into the file if no filler is given or only one block was created
		        for(int i=0; i<splittedActlines.size(); i++) {
		        	for(int j1 = 0; j1 < splittedActlines.get(i).size() ; j1++) {
		        		bw.write(splittedActlines.get(i).get(j1) + "\r\n");
		        	}
		        }
	        } else {
	        	//write the results into the file but every time a new block starts, fill it with the correct date and the filler
	        	for(int i = 0; i < splittedActlines.size() ; i++) {
	        		for(int k = 0; k < splittedActlines.get(i).size(); k++) {
		        		System.out.println( i + k + splittedActlines.get(i).get(k));
	        			bw.write(splittedActlines.get(i).get(k) + "\r\n");
	        		}
	        		try {
	        			String last = splittedActlines.get(i).get(  splittedActlines.get(i).size()   );
	        			String next = splittedActlines.get(i+1).get(  splittedActlines.get(i+1).size()   );
	        			ArrayList<String> fillingStrings = getDateTimeBetween(last, next);
	        			for (int l = 0; l < fillingStrings.size(); l++) {
	        				bw.write(fillingStrings.get(l) + ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler + ";"+ ini.filler  + "\r\n");
	        			}
	        		} catch (Exception e2) {
	        			//could not access splittedActlines.get(i+1) -> filling done
	        			break;
	        		}
	        	}
	        }
	        bw.close();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		progressBar.setValue(100);
		lblProgress1.setText("Successfully created activity.act");
		lblProgress1.setForeground(Color.GREEN);
		lblProgress2.setText("");
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.exit(0);
	}


	public ArrayList<String> getSpectrafromCard(String _IP){
		ArrayList<String> filenames = new ArrayList<String>();
		
		try {
			TimeUnit.MILLISECONDS.sleep(500);
			telnetTunnel con = new telnetTunnel(_IP, 23);
			TimeUnit.MILLISECONDS.sleep(100);
			String directory = con.get_dir() ;
			TimeUnit.MILLISECONDS.sleep(100);
			System.out.println("files on the SD card:" + directory);
			//String[] dirList = directory.split(" ");
			int s = 0;
			try {
				while(!directory.isEmpty()) {	
					filenames.add(directory.substring(s, s+13));
					s+=13;
				}
			} catch (StringIndexOutOfBoundsException e) {
				directory = null;
			}
			lblProgress1.setText("Connected to " + _IP);
			return filenames;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.print("connection error");
			lblProgress1.setText("Could not get Files from " + _IP);
			lblProgress1.setForeground(Color.RED);
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			System.exit(1);
			return filenames;
		}
		
	}

	public ArrayList<String> calcStockburger(ArrayList<String> extLines,  int points) {
		System.out.println("Calculating these lines : ");
		for ( int i =0; i< extLines.size(); i++) {
			System.out.println(extLines.get(i));
		}
		
		//load *.ini file
		iniFile ini = new iniFile();
		
		long[] timeDiffs = new long[extLines.size()]; //LT, total, window, Po212, timeDifference, Ac
		int[] LTs = new int[extLines.size()];
		int[] totals = new int[extLines.size()];
		int[] windows = new int[extLines.size()];
		int[] Po212s = new int[extLines.size()];
		int[] edges = new int[extLines.size()];
		
		double[] activities = new double[extLines.size()];
		double[] t1s = new double[extLines.size()];
		double[] t2s = new double[extLines.size()];
		double[] t3s = new double[extLines.size()];
		double[] pressures = new double[extLines.size()];
		double[] Acs = new double[extLines.size()];
		double[] act_ps = new double[extLines.size()];
		double[] fluxs = new double[extLines.size()];
		double[] dAcdts = new double[extLines.size()];
		double[] Acs_deriv = new double[points*2+1];
		double[] dt_deriv = new double[points*2+1];
		
		ArrayList<String> actlines = new ArrayList<String>();
		if(extLines.size()<3) {
			System.out.println("Extractfile had only 2 lines.");
			actlines.add("");
			return actlines;
		}
		
		for(int i = 1; i < extLines.size(); i++) {
			//calculate time difference between two measurement points
			//if the difference is to small (<60) oder too large (>1800) there is a duplicate line or a missing one
			//-> delete that line or split the extLines up
			String DateTime1 = extLines.get(i-1).split(";")[0];
    		String DateTime2 =  extLines.get(i).split(";")[0];
    		DateFormat formatter = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
    		long diffInSeconds = 0;
			try {
				diffInSeconds = (formatter.parse(DateTime2).getTime() - formatter.parse(DateTime1).getTime())/1000;
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			timeDiffs[i] = diffInSeconds;
		}
		
		
		for(int i = 1; i < extLines.size(); i++) {
    		//Date Time; Lifetime;ADC1; StdADC1; T1; StdT1;T2; StdT2;T3; StdT3;Rn1;Rn2;Rn3;Rn4;ADC2; StdADC2; ADC3; StdADC3; Counter1;Counter2;FluxSlope;FluxOffset;ADC2Slope;ADC2Offset;ADC3Slope;ADC3Offset;Temp1Slope;Temp1Offset;Temp2Slope;Temp2Offset;Temp3Slope;Temp3Offset;Counter1Slope;Counter1Offset;Counter2Slope;Counter2Offset;ID
    		//get Date Time of String 04.07.2018 13:00:00
			//save every intermediate value in its own list
			
			//get LT, total, window, Po212, edge, temp1, temp2, temp3, pressure
			LTs[i] = Integer.parseInt(extLines.get(i).split(";")[1].replaceAll("\\s+",""));

			//RN1
			totals[i] = Integer.parseInt(extLines.get(i).split(";")[10].replaceAll("\\s+",""));

			//RN2
			windows[i] = Integer.parseInt(extLines.get(i).split(";")[11].replaceAll("\\s+",""));
			
			//RN3
			Po212s[i]= Integer.parseInt(extLines.get(i).split(";")[12].replaceAll("\\s+",""));
			
			//edge
			edges[i] = Integer.parseInt(extLines.get(i).split(";")[13].replaceAll("\\s+",""));
			
			//t1,t2,t3 in kelvin
			t1s[i] = Double.parseDouble(extLines.get(i).split(";")[4].replaceAll("\\s+","")) -273.2;
			t2s[i] = Double.parseDouble(extLines.get(i).split(";")[6].replaceAll("\\s+","")) -273.2;
			t3s[i] = Double.parseDouble(extLines.get(i).split(";")[8].replaceAll("\\s+","")) -273.2;
			
			//pressure -> fill with 0.0
			//not available currently
			pressures[i] = 0.0;
					
			//calculate Ac (activity on filter
			Acs[i] = 0.942 * (Double.valueOf(totals[i]) - Double.valueOf(windows[i]) - (Double.valueOf(Po212s[i]) / 0.4984646)) / Double.valueOf(LTs[i]);   //0.778 * 0.6407 = 0.4984646 
			
			//calculate flux in m³/s
			fluxs[i] = Double.valueOf(extLines.get(i).split(";")[2].replaceAll("\\s+",""));
			//multiply by slope, add offset and convert to m³/s
			fluxs[i] = ((ini.fluxslope * fluxs[i]) + ini.fluxoffset) / 3600000; 
			
			//calculate act_p
			act_ps[i] = ini.disequilibrium / (ini.solidangle * fluxs[i] * 4302);
		}
		
		for( int i = 1; i < extLines.size(); i++) {
			//use intermediates to calculate the derivation (dAc/dt)
			//kick the first #points out, because there are not enough values before to calculate the derivation
			if((i - 1 - points) < 0) {
				//not enough points to calculate derivation
				continue;
			}
			
			//create lists for saving the points, x = time Difference; y = Ac
			//and give them to LinearRegression(x[], y[])
			
			Acs_deriv = null;
			dt_deriv = null;
			Acs_deriv = new double[points*2+1];
			dt_deriv = new double[points*2+1];
			try {
				double n = 2 * points + 1; //3,5,7,...
				for (int x = 0; x < n; x++) {
					// -points, ... , 0 , ... points
					//test wether it is a spectra before, the current spectrum or one of the next spectra to avoid out of bounds exception
					//looks complicated but makes sense if you think about it
					if(x < (n/2-0.5)) {
						Acs_deriv[x] = Acs[(int) (i - ( n/2-0.5 - x))];
						dt_deriv[x] = timeDiffs[(int) (i - ( n/2-0.5 - x))];
					}
					if(x > n/2) {
						Acs_deriv[x] = Acs[(int) (x-(n/2-0.5)+i)];
						dt_deriv[x] = timeDiffs[(int) (x-(n/2-0.5)+i)];
					} 
					if (x == (n/2-0.5)){
						Acs_deriv[x] = Acs[i];
						dt_deriv[x] = timeDiffs[i];
					}
				}
			} catch (Exception OutOfBounds) {
				//not enough points to calculate derivation
				continue;
			}
			
			//until now, dt_deriv is filled with 1800, 1800, 1800,...
			//->fill it with 0, 1800, 3600, ... for the derivation
			double[] tmp = new double[Acs_deriv.length];
			for(int j = 0; j < Acs_deriv.length; j++) {
				if (j == 0) {
					tmp[j] = 0;
				} else {
					tmp[j] = tmp[j-1] + dt_deriv[j];
				}
			}
			dt_deriv = tmp;
			tmp = null;
			
			//calculate derivation
			LinearRegression LR = new LinearRegression(dt_deriv, Acs_deriv);
			dAcdts[i] = LR.slope();
			
			//calculate activity = act_p * (4302 * dAc/dt +Ac)
			activities[i] = act_ps[i] * ((4302 * dAcdts[i]) + Acs[i]);
			
			//gather results into a String line and save it in actlines[]
			//Stoptime,Activity [Bq/m3], Ac[dps],Ac/dt,Total, Window, Edge, temp1[C], temp2[C], temp3[C], Pressure[mbar], LifeTime[sec], Flux[m3/s], ID
			String actline = "";
			NumberFormat formatter = new DecimalFormat("#0.0000000000000000000");
			
			actline += extLines.get(i).split(";")[0] + "; " + activities[i] + "; " + Acs[i] + "; " 
					+ dAcdts[i] + "; " + totals[i] + "; " + windows[i] + "; " + edges[i] + "; " + t1s[i] + "; " 
					+ t2s[i] + "; " + t3s[i] + "; " + pressures[i] + "; " + LTs[i] + "; " + formatter.format(fluxs[i]).replaceAll(",", ".") + "; " 
				    +  extLines.get(i).split(";")[36] /*ID*/ ;
			actlines.add(actline);
			System.out.println("adding " + actline);
		}
		return actlines;
	}

	public ArrayList<String> getDateTimeBetween ( String DT1, String DT2) throws ParseException{
		//helper function which gets the Date Time of a String (split it at ";" and convert to timeformat
		//and returns a list of Strings with timestamps between
		ArrayList<String> results = new ArrayList<String>();
		DateFormat formatter = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
        Date first = formatter.parse(DT1.split(";")[0]);
        Date last = formatter.parse(DT2.split(";")[0]);
        long diff = last.getTime() - first.getTime();
		if (diff < 1800000) {
			return results;
		}
		int fillingpoints = 0;
		fillingpoints = (int) (diff/1800000);
		for(int i = 0; i<fillingpoints; i++) {
			Date newDate = new Date(first.getTime() + 1800000);
			results.add(newDate.toString());
			 System.out.println("fill this :" + newDate.toString());
		}
		return results;
	}
	
	
	//helper function for copying files (flagged spectra into subfolder etc)
	private static void copyFile(File source, File dest) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source); 
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} catch (Exception ioex) {
			System.out.println("Could not copy " + source.getName() + " to " + dest.getName());
		} finally {
			input.close();
			output.close();
		}
	}
	
}