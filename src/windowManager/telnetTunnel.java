package windowManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.apache.commons.net.telnet.*;
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
	//public TelnetClient soc2 = null;
	
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
		//gets an array of filenames and returns an array of Strings, consisting of the spectra
		//the strings needs to be saved as spectra
		
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
		for(int j = 0; j< FileList.size(); j++) {
			progressBar.setValue((int) progress * (j+1));
			lblProgress2.setText("Getting " + FileList.get(j));
			System.out.println("Getting " + FileList.get(j));
			TimeUnit.MILLISECONDS.sleep(50);
			writeBuffer.write("snd" + " " + FileList.get(j));
			writeBuffer.newLine();
			writeBuffer.flush();
			TimeUnit.MILLISECONDS.sleep(500);

			//////////////////////////////////////////////////////////
			//gather the data
			///////////////////////////////////////////////////////////
			TimeUnit.MILLISECONDS.sleep(50);
			int tmp;
			String result="";
			TimeUnit.MILLISECONDS.sleep(50);
			
			//read telnet buffer byte by byte and convert it to char
			//if buffer is not ready, wait and try again, if its still not ready, end of file is reached
			//checking if tmp == -1 does not work
			
			while(true) {
				tmp = readBuffer.read();
				result += Character.toString ((char) tmp);
				if(!readBuffer.ready()) {
					TimeUnit.MILLISECONDS.sleep(70);
					System.out.println("waiting for telnet buffer");
					if(!readBuffer.ready())
						break;
				}
			}
			
			//////////////////////////////////////////////////////////
			//compute the correct output string out of the telnet data
			//by removing linebreaks ("\n") and spaces
			///////////////////////////////////////////////////////////
			String cleanResult = "";
			for(int i = 0; i<result.split("\n").length; i++) {
				if(!result.split("\n")[i].isEmpty() && !result.split("\n")[i].contains("Password")) {
					cleanResult+=result.split("\n")[i].trim() + System.lineSeparator();
				}
			}
			Files.add(cleanResult);
		}
		writeBuffer.close();
		readBuffer.close();
		this.soc.close();
		return Files;
	}
}


