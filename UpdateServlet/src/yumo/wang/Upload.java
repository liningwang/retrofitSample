package yumo.wang;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class Upload extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public Upload() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("wang doPost");
		//返回值，是图片在服务器上保存的url地址，通过这个url地址，可以下载这个图片。
		//方法的第二个参数，是图片在服务器上保存到工程目录下的目录。
		String url = saveFile(request, "hello");
		response.getWriter().write(url);
	}

	private String saveFile(HttpServletRequest request, String path){
		String result = null;
		String uuidname = null;
		try {
			//1.上传文件
			String upload = this.getServletContext().getRealPath(path);
			String temp = this.getServletContext().getRealPath("WEB-INF/temp");
			new File(temp).mkdirs();
			//--创建工厂设置内存缓冲区的大小和临时文件夹的位置
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024*100);
			factory.setRepository(new File(temp));
			
			//--获取文件上传核心类,解决文件名乱码/设置文件大小限制
			ServletFileUpload fileUpload = new ServletFileUpload(factory);
			fileUpload.setHeaderEncoding("utf-8");
//			fileUpload.setFileSizeMax(1024*1024*100);
//			fileUpload.setSizeMax(1024*1024*200);
			
			//--检查是否是正确的文件上传表单
			if(!fileUpload.isMultipartContent(request)){
				throw new RuntimeException("请使用正确的表单进行上传!");
			}
			
			//--解析request
			List<FileItem> list = fileUpload.parseRequest(request);
			
			//--遍历list,获取FileItem进行解析
			for(FileItem item : list){
				if(item.isFormField()){//普通字段项
					String name = item.getFieldName();
					String value = item.getString("utf-8");
					System.out.println("name " + name + " value " + value);
				}else{//文件上传
					System.out.println("upload file");
					//--uuidname防止文件名重复
					String realname = item.getName();
					uuidname = UUID.randomUUID().toString()+"_"+realname;
					
					//--获取输入流
					InputStream in = item.getInputStream();
					
					//--分目录存储防止一个文件夹中文件过多
//					String hash = Integer.toHexString(uuidname.hashCode());
//					String savepath = "/WEB-INF/upload";
//					for(char c : hash.toCharArray()){
//						upload+="/"+c;
//						savepath+="/"+c;
//					}
					new File(upload).mkdirs();
					
					//--获取输出流
					OutputStream out = new FileOutputStream(new File(upload,uuidname));
					
					//--流对接上传
					byte[] b = new byte[1024];
					while(in.read(b) != -1){
						out.write(b);
					}
					//--删除临时文件
					out.close();
					in.close();
					item.delete();
					System.out.println("upload finish");
					
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		String localIp=request.getLocalAddr();//获取本地ip
		int localPort=request.getLocalPort();//获取本地的端口
		String path1 = this.getServletContext().getContextPath();
		System.out.println("ip " + localIp + " port" + localPort + " path "+path1 );
		result = "http://"+localIp+":"+localPort+path1+"/" + path +"/"+uuidname;
		return result;
	}
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
