package org.zframework;

import java.io.File;
import java.io.FilenameFilter;

public class GenderIconCss {
	public static void main(String[] args) {
		String path = "E:/MyEclipse/Workspaces/MyEclipse 8.5/zFrame/webapps/resources/framework/images/icons";
		final String iconName = "menu";
		final String targetName = "menu";
		File file = new File(path);
		File[] files = file.listFiles(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				if(name.startsWith(iconName))
					return true;
				return false;
			}
		});
		for(File f : files){
			String name = f.getName().replace(".png", "");
			name = name.replace(".gif", "");
			name = name.replace(iconName, targetName);
			String icon = ".icon-" + name.replace("_", "-")+"{\n";
			StringBuffer sb = new StringBuffer(icon);
			sb.append("\tbackground:url('../../images/icons/"+name+".png') no-repeat;\n");
			sb.append("}");
			if(targetName.length()>0 && !iconName.equals(targetName))
				f.renameTo(new File(path+"/"+name+".png"));
			System.out.println(sb.toString());
		}
	}
	/*public static void main(String[] args) {
		File file = new File("E:/MyEclipse/Workspaces/MyEclipse 8.5/zFrame/webapps/resources/framework/css/easyui/bootstrap/easyui.css");
		try {
			List<String> urls = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(file));
			while(br.ready()){
				String line = br.readLine();
				if(line.indexOf("images/")>0){
					String url = line.substring(line.indexOf("images/"),line.indexOf(".")+4);
					urls.add(url);
				}
			}
			br.close();
			int bytesum = 0;
			int byteread = 0;
			for(String url : urls){
				URL netUrl = new URL("http://jeasyui.com/easyui/themes/bootstrap/"+url);
				URLConnection urlCon = netUrl.openConnection();
				InputStream is = urlCon.getInputStream();
				
				FileOutputStream fos = new FileOutputStream("E:/MyEclipse/Workspaces/MyEclipse 8.5/zFrame/webapps/resources/framework/css/easyui/bootstrap/"+url);
				byte[] buffer = new byte[1204];
				while((byteread = is.read(buffer)) != -1) {
					 bytesum += byteread;
					 fos.write(buffer, 0, byteread);
				}
				System.out.println("文件"+url+"下载成功!");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
