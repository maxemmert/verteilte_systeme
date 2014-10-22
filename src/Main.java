import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import net.dubioso.dhbw.rotos.util.BuildTLV;
import net.dubioso.dhbw.rotos.util.ProtocolConstants.TLVType;

public class Main {

	public static void main(String[] args) {

		System.out.print("Geben Sie den Host ein: ");
		String host = readLineFromConsole();
		System.out.println("Geben Sie den Port ein: ");
		String port = readLineFromConsole();
		System.out.println("Host: " + host);
		System.out.println("Port: " + port);

		String[] msgs = new String[1000];
		msgs[0] = "e0";
		msgs[1] = "82";
		msgs[2] = "00";
		msgs[3] = "12";
		msgs[4] = "c2";
		msgs[5] = "82";
		msgs[6] = "00";
		msgs[7] = "04";
		msgs[8] = "66";
		msgs[9] = "72";
		msgs[10] = "65";
		msgs[11] = "69";
		msgs[12] = "c0";
		msgs[13] = "82";
		msgs[14] = "00";
		msgs[15] = "06";
		msgs[16] = "48";
		msgs[17] = "61";
		msgs[18] = "6c";
		msgs[19] = "6c";
		msgs[20] = "6f";
		msgs[21] = "21";

		// e2 82 00 12 c2 82 00 04 66 72 65 69 c0 82 00 06 48 61 6c 6c 6f 21
		int needed = 4 + 4 * msgs.length;
		for (String msg : msgs)
			needed += msg.length() * 4;
		byte[] data = new byte[needed];
		BuildTLV main = new BuildTLV(TLVType.MESSAGE_LIST, data, 0);
		System.out.println("main TLV before adding messages:");
		System.out.println(main.toFullString());
		BuildTLV next = null;
		for (String msg : msgs) {
			next = new BuildTLV(TLVType.MESSAGE, data, main.getEnd());
			next.setTextValue(msg, "UTF-8");
			main.addToLength(next.getSize());

			System.out.println("added " + next.toFullString());
		}

		System.out.println("main TLV with all messages:");
		System.out.println(main.toFullString());

		Socket socket = null;
		try {
			socket = new Socket("localhost", 49974);

			sendTLV(next, socket);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void sendTLV(BuildTLV next, Socket socket)
			throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		printWriter.print(next.toFullString());
		printWriter.flush();
	}

	private static String readLineFromConsole() {
		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));
		String zeile = null;
		try {
			zeile = console.readLine();
		} catch (IOException e) {
			// Sollte eigentlich nie passieren
			e.printStackTrace();
		}
		return zeile;
	}

}
