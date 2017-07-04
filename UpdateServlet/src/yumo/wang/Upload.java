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
		//����ֵ����ͼƬ�ڷ������ϱ����url��ַ��ͨ�����url��ַ�������������ͼƬ��
		//�����ĵڶ�����������ͼƬ�ڷ������ϱ��浽����Ŀ¼�µ�Ŀ¼��
		String url = saveFile(request, "hello");
		response.getWriter().write(url);
	}

	private String saveFile(HttpServletRequest request, String path){
		String result = null;
		String uuidname = null;
		try {
			//1.�ϴ��ļ�
			String upload = this.getServletContext().getRealPath(path);
			String temp = this.getServletContext().getRealPath("WEB-INF/temp");
			new File(temp).mkdirs();
			//--�������������ڴ滺�����Ĵ�С����ʱ�ļ��е�λ��
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024*100);
			factory.setRepository(new File(temp));
			
			//--��ȡ�ļ��ϴ�������,����ļ�������/�����ļ���С����
			ServletFileUpload fileUpload = new ServletFileUpload(factory);
			fileUpload.setHeaderEncoding("utf-8");
//			fileUpload.setFileSizeMax(1024*1024*100);
//			fileUpload.setSizeMax(1024*1024*200);
			
			//--����Ƿ�����ȷ���ļ��ϴ���
			if(!fileUpload.isMultipartContent(request)){
				throw new RuntimeException("��ʹ����ȷ�ı������ϴ�!");
			}
			
			//--����request
			List<FileItem> list = fileUpload.parseRequest(request);
			
			//--����list,��ȡFileItem���н���
			for(FileItem item : list){
				if(item.isFormField()){//��ͨ�ֶ���
					String name = item.getFieldName();
					String value = item.getString("utf-8");
					System.out.println("name " + name + " value " + value);
				}else{//�ļ��ϴ�
					System.out.println("upload file");
					//--uuidname��ֹ�ļ����ظ�
					String realname = item.getName();
					uuidname = UUID.randomUUID().toString()+"_"+realname;
					
					//--��ȡ������
					InputStream in = item.getInputStream();
					
					//--��Ŀ¼�洢��ֹһ���ļ������ļ�����
//					String hash = Integer.toHexString(uuidname.hashCode());
//					String savepath = "/WEB-INF/upload";
//					for(char c : hash.toCharArray()){
//						upload+="/"+c;
//						savepath+="/"+c;
//					}
					new File(upload).mkdirs();
					
					//--��ȡ�����
					OutputStream out = new FileOutputStream(new File(upload,uuidname));
					
					//--���Խ��ϴ�
					byte[] b = new byte[1024];
					while(in.read(b) != -1){
						out.write(b);
					}
					//--ɾ����ʱ�ļ�
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
		String localIp=request.getLocalAddr();//��ȡ����ip
		int localPort=request.getLocalPort();//��ȡ���صĶ˿�
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
