package windowManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class telnetTunnel {
	
	/*opening connection to the monitor via an telnet socket
	 * unfortunantely the connection has to be redone for every function
	 * or the connection will be lost.
	 */
	
	public Socket soc = null;
	public BufferedReader readBuffer = null;
	public BufferedWriter writeBuffer = null;
	public String IP = null;
	public int port = 23;
	
	telnetTunnel(String IP, int port) {
		this.IP = IP;
		this.port = port;
	}
	
	public String get_dir() throws IOException, InterruptedException {
		//open socket connection to monitor
		System.out.println("opening socket");
		soc = new Socket(IP,port);	
		
		//create buffered writer
		BufferedReader readBuffer = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		BufferedWriter writeBuffer = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
		//wait and enter password, standard pw is 4118
		TimeUnit.MILLISECONDS.sleep(100);
		writeBuffer.write("4118\r\n");
		writeBuffer.flush(); 
		TimeUnit.MILLISECONDS.sleep(100);
		
		//send 'dir' to get a list of all files on the SD card
		writeBuffer.write("dir");
		writeBuffer.newLine();
		writeBuffer.flush();
		TimeUnit.MILLISECONDS.sleep(100);
		// getting the first 4 lines, password etc
		String directory = readBuffer.readLine();
		directory = readBuffer.readLine();
		directory = readBuffer.readLine();
		directory = readBuffer.readLine();
		
		//check every 500ms if a new name arrived
		String tmp = null;
		while(true){
			TimeUnit.MILLISECONDS.sleep(500);
			tmp = readBuffer.readLine();
			try {
				//tmp is null sometimes, connection error
				if(tmp.isEmpty()) {
					//no new files read
					break;
				} else {
					directory = directory+tmp;
					System.out.println(directory);
					tmp = null;
				}
			} catch (Exception e) {
				directory = null;
				break;
			}
		}
		soc.close();
		return directory;
	}

	public ArrayList<String> snd(ArrayList<String> FileList, JProgressBar progressBar, JLabel lblProgress2) throws IOException, InterruptedException {
		//gets an array of filenames and returns an array von Strings, consisting of the spectra
		//the strings needs to be safed as spectra
		
		ArrayList<String> Files = new ArrayList<String>();
		double progress = 100/FileList.size();
		
		//open socket connection to monitor
		System.out.println("opening socket");
		soc = new Socket(IP,port);	
		//create buffered writer
		BufferedReader readBuffer = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		BufferedWriter writeBuffer = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
		//wait and enter password, standard pw is 4118
		TimeUnit.MILLISECONDS.sleep(100);
		writeBuffer.write("4118\r\n");
		writeBuffer.flush(); 
		TimeUnit.MILLISECONDS.sleep(100);
		
		////////////////////////////////////
		//get the spectra for every filename
		////////////////////////////////////
		int errors = 0;
		for(int j = 0; j< FileList.size(); j++) {
			progressBar.setValue((int) progress * (j+1));
			lblProgress2.setText("Getting " + FileList.get(j));
			System.out.println("Getting " + FileList.get(j));
			TimeUnit.MILLISECONDS.sleep(50);
			writeBuffer.write("snd" + " " + FileList.get(j));
			writeBuffer.newLine();
			writeBuffer.flush();
			TimeUnit.MILLISECONDS.sleep(500);

			int tmp = readBuffer.read();
			String result="";
			while(tmp != -1 && readBuffer.ready()) {
				result += Character.toString ((char) tmp);
				tmp = readBuffer.read();
			}
			
			//////////////////////////////////////////////////////////
			//compute the correct output string out of the telnet data
			///////////////////////////////////////////////////////////
			
			//split the gathered data at whitespaces (linebreaks and spaces)
			//kick out everything that is whitespace
			//String.replace does not work with telnet data as far as I know, thats why I use this workaround
			ArrayList<String> splittedResult = new ArrayList<String>();
			for(int i=0; i<result.split("\\s").length; i++) {
				//filter out the returned "password ?" line from the monitor, as well as empty spaces
				if(!result.split("\\s")[i].equals("\\s") && !result.split("\\s")[i].trim().isEmpty() &&  !result.split("\\s")[i].trim().contains("?") && !result.split("\\s")[i].trim().contains("Password")) {
					splittedResult.add(result.split("\\s")[i].trim());
				}	
			}
			if(splittedResult.size()<159) {
				//retrieved data is not complete
				//try again if its the first time
				if(errors == 0) {
					j = j-1;
					continue;
				} else {
					errors = 1;
					continue;
				}
			}
			
			//combine the splitted result into a String, containing the correct line seperators
			//each line needs to be tested so the finalResult is according to what we need
			String finalResult = "";
			for(int i = 0; i< splittedResult.size(); i++) {
				finalResult += splittedResult.get(i);
				if(Arrays.asList(0, 3, 134, 135, 136, 137, 138, 139, 140, 141, 143, 144, 145, 146, 147, 148, 149, 150, 152, 153, 154, 155, 156, 157).contains(i)) {
					finalResult+=" ";
				}
				if(!Arrays.asList(0, 3, 134, 135, 136, 137, 138, 139, 140, 141, 143, 144, 145, 146, 147, 148, 149, 150, 152, 153, 154, 155, 156, 157).contains(i))
					finalResult += System.lineSeparator();
			}
			Files.add(finalResult);
			System.out.println(finalResult);
		}
		writeBuffer.close();
		readBuffer.close();
		this.soc.close();
		return Files;
	}
}


