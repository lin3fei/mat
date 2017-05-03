package com.cmcciot.mat.filemanage.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cmcciot.mat.filemanage.bean.FileUploadBean;
import com.cmcciot.mat.filemanage.cache.FileCache;
import com.cmcciot.mat.filemanage.cache.Md5Cache;
import com.cmcciot.mat.filemanage.service.DigestAuthService;
import com.cmcciot.mat.filemanage.service.FileService;
import com.cmcciot.mat.filemanage.utils.DigestAuthUtils;
import com.cmcciot.mat.filemanage.utils.IpTools;
import com.cmcciot.mat.filemanage.utils.KeyUtil;
import com.cmcciot.mat.filemanage.utils.PropertyUtil;
import com.cmcciot.mat.filemanage.utils.StringUtil;

/**
 * <文件服务管理类> <功能详细描述>
 * 
 * @author 王文灿
 * @version [版本号, 2014年4月23日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Controller
@RequestMapping
public class FileController {
	
	private Log logger = LogFactory.getLog(getClass());

	private String uploadFilePath;
	
	private String life;
	
	@Resource
	private DigestAuthService digestAuth;
	
	@Resource
	private FileService fileService;

	/**
	 * <获取文件上传、下载地址>
	 * 
	 * @return
	 * @throws ServletException,
	 *             IOException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "getUrl")
	public void getUrl(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sourceIp = request.getRemoteAddr();
		String[] headerEntries = DigestAuthUtils.splitIgnoringQuotes(request.getHeader("HOA_auth"), ',');
		Map<String, String> headerMap = DigestAuthUtils.splitEachArrayElementAndCreateMap(headerEntries, "=", "\"");
		String key = headerMap.get("key");
		try {
			// 对于内部子服务平台进行鉴权
			if (digestAuth.digestAuth(sourceIp, key)) {
				String urlType = request.getParameter("urlType");
				// 获取Http请求，包括路由ID、事务ID
				String devID = request.getParameter("devID");
				String contentID = request.getParameter("contentID");
				life = PropertyUtil.getValue("life");
				key = PropertyUtil.getValue("key");

				// 获取系统时间戳
				Date currentTime = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
				String dateString = formatter.format(currentTime);

				// 获得服务器的ip与端口
				String ip = InetAddress.getLocalHost().getHostAddress();
				System.out.println(ip);
				System.out.println(KeyUtil.getIP());
				if (ip.equals("127.0.0.1")) {
					ip = KeyUtil.getIP();
				}
				logger.info("文件服务管理：服务器ip地址为：" + ip);
				String url = "http://" + ip + ":" + request.getServerPort();
				if (!StringUtil.isEmpty(PropertyUtil.getValue("current.domain"))) {
					url = "http://" + PropertyUtil.getValue("current.domain");
				}
				// 判断请求url地址的类型，生成相应的url
				if (urlType != null && !urlType.equals("")) {
					if (urlType.equals("0")) {
						try {
							uploadFilePath = PropertyUtil.getValue("path");
							File firstFolder = new File(uploadFilePath + devID);
							if (!firstFolder.exists()) {
								firstFolder.mkdir();
								logger.info("文件服务管理：文件夹创建成功，目录：" + firstFolder.getPath());
							}

							// 生成需要MD5加密的部分
							String md5Paramet = "/filemanage/imageupload/" + contentID + "/" + devID + "/" + dateString
									+ "/" + life;

							String md5 = KeyUtil.makeMD5(md5Paramet + "&" + key);
							// 生成上传url
							String uploadUrl = url + md5Paramet + "/" + md5;
							logger.info("文件服务管理：上传url地址：" + uploadUrl);
							Md5Cache md5cache = new Md5Cache();
							md5cache.setMd5(md5);
							md5cache.setTimeTemp(dateString);
							FileCache.FILEUPLOADCACHEMAP.put(contentID, md5cache);

							response.setContentType("application/json;charset=UTF-8");
							response.setCharacterEncoding("UTF-8");
							response.setStatus(200); // 设置返回状态为OK
							JsonObject reqobj = Json.createObjectBuilder().add("description", "success")
									.add("errorCode", 0).add("URL", uploadUrl).add("devID", devID)
									.add("contentID", contentID).build();

							response.getWriter().print(reqobj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (urlType.equals("1")) {
						try {
							if (!StringUtil.isEmpty(PropertyUtil.getValue("downloadUrl"))) {
								url = "http://" + PropertyUtil.getValue("downloadUrl");
							}
							String downloadUrl = "/filemanage/imagedownload/" + contentID + "/" + devID + "/"
									+ dateString + "/" + life;
							// 生成下载url
							String downloadFilePath = url + downloadUrl;
							logger.info("文件服务管理：下载url地址：" + downloadFilePath);

							response.setContentType("application/json;charset=UTF-8");
							response.setCharacterEncoding("UTF-8");
							response.setStatus(200); // 设置返回状态为OK
							JsonObject reqobj = Json.createObjectBuilder().add("description", "success")
									.add("errorCode", 0).add("URL", downloadFilePath).add("devID", devID)
									.add("contentID", contentID).build();

							response.getWriter().print(reqobj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						response.setContentType("application/json;charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setStatus(200); // 设置返回状态为OK

						response.getWriter().write("{\"errorCode\":\"5003\",\"description\":\"error\"}");
						logger.info("文件服务管理：url请求类型出错！");
					}
				}
			} else {
				logger.info("文件服务管理：自服务内部鉴权未通过！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <文件二进制流上传，将文件保存在指定目录>
	 * 
	 * @return
	 * @throws ServletException,
	 *             IOException
	 * @throws ParseException
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/imageupload/{contentID}/{devID}/{stamptime}/{life}/{cry}")
	public void uploadImage(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("contentID") String contentID, @PathVariable("devID") String devID,
			@PathVariable("stamptime") String stamptime, @PathVariable("life") String life,
			@PathVariable("cry") String remoteCry) throws ServletException, IOException {
		String apiip = IpTools.getIpAddr(request);
		logger.info("进入上传图片业务，访问ip：" + apiip);
		// 对上传地址中的cry校验、url校验、有效期校验
		Md5Cache md5cache = (Md5Cache) FileCache.FILEUPLOADCACHEMAP.get(contentID);
		if (md5cache == null) {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(200); // 设置返回状态为OK
			response.getWriter().write("{\"errorCode\":\"5004\",\"description\":\"error\"}");
			logger.info("文件服务管理：此地址已被使用过，已经过期失效！");
		} else {
			String localCry = md5cache.getMd5();
			String requestUri = request.getRequestURI().substring(0, request.getRequestURI().lastIndexOf("/")) + "&"
					+ PropertyUtil.getValue("key");
			logger.info("文件服务管理：请求uri：" + requestUri);
			logger.info("文件服务管理：请求uri的MD5：" + KeyUtil.makeMD5(requestUri));
			logger.info("文件服务管理：本地存放的MD5：" + localCry);

			try {
				if (remoteCry.equals(localCry)) {
					logger.info("文件服务管理：密钥鉴权通过！");
					if (localCry.equals(KeyUtil.makeMD5(requestUri))) {
						logger.info("文件服务管理：请求uri鉴权通过！");
						// 校验请求时间是否在有效期内
						if (KeyUtil.compare_date(life, stamptime) < 0) {
							logger.info("文件服务管理：时间戳已经过期！");
							response.setContentType("text/html;charset=UTF-8");
							response.setCharacterEncoding("UTF-8");
							response.setStatus(200); // 设置返回状态为OK
							response.getWriter().write("{\"errorCode\":\"5004\",\"description\":\"error\"}");
						} else {
							logger.info("文件服务管理：获取上传文件流！");
							MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
							// 获得第1张图片（根据前台的name名称得到上传的文件）
							MultipartFile uploadFile = multipartRequest.getFile(contentID);

							if (null == uploadFile || uploadFile.isEmpty()) {
								logger.info("文件服务管理：上传的文件为空文件！");
								response.setContentType("text/html;charset=UTF-8");
								response.setCharacterEncoding("UTF-8");
								response.setStatus(200); // 设置返回状态为OK
								response.getWriter().write("{\"errorCode\":\"5005\",\"description\":\"error\"}");
							} else {
								if (uploadFile.getSize() <= Integer.parseInt(PropertyUtil.getValue("image.max_size"))) {
									logger.info("文件服务管理：开始保存文件！" + uploadFile.getOriginalFilename());
									InputStream inStream = uploadFile.getInputStream();
									// 对contentID进行处理加工，对图片保存名称进行重命名
									String name = contentID.substring(0, contentID.lastIndexOf("_"));
									String type = contentID.substring(contentID.lastIndexOf("_") + 1).toLowerCase();
									String filename = name + "." + type;
									uploadFilePath = PropertyUtil.getValue("path") + "/" + devID + "/";
									// 生成绝对路径
									File tempFile1 = new File(uploadFilePath);
									if (!tempFile1.exists()) {
										tempFile1.mkdirs();
									}
									// 如果服务器已经存在和上传文件同名的文件，则输出提示信息
									File tempFile = new File(uploadFilePath + filename);

									// 判断文件是否存在，如果存在则删除
									if (tempFile.exists()) {
										boolean delResult = tempFile.delete();
										logger.info("文件服务管理：删除已存在的文件：" + delResult);
									}
									OutputStream fileOut = new FileOutputStream(tempFile);
									byte[] bt = new byte[1024];
									int iRead = 0;
									while ((iRead = inStream.read(bt)) > 0) {
										fileOut.write(bt, 0, iRead); // 向服务端文件写入字节流
									}

									logger.info("文件服务管理：文件上传完成后删除内存MD5！");
									FileCache.FILEUPLOADCACHEMAP.remove(contentID);
									logger.info("文件服务管理：返回flase表示删除成功："
											+ FileCache.FILEUPLOADCACHEMAP.containsKey(contentID));

									logger.info("文件服务管理：文件上传完成~~~");

									// 上传成功存入信息到DB
									String ip = PropertyUtil.getValue("localIp");
									logger.info("保存服务器ip：" + ip);
									FileUploadBean fileUploadBean = new FileUploadBean();
									fileUploadBean.setIp(ip);
									fileUploadBean.setFileName(filename);
									fileUploadBean.setCreateTime(String.valueOf(System.currentTimeMillis()));
									fileUploadBean.setFilePath(uploadFilePath);
									fileService.addFileUploadInfo(fileUploadBean);

									response.setContentType("text/html;charset=UTF-8");
									response.setCharacterEncoding("UTF-8");
									response.setStatus(200); // 设置返回状态为OK
									response.getWriter().write("{\"errorCode\":\"0\",\"description\":\"success\"}");
									fileOut.flush();
									fileOut.close(); // 关闭FileOutputStream对象
									inStream.close(); // InputStream对象

								} else {
									logger.info("文件服务管理：上传文件大小超过规定！");
									response.setContentType("text/html;charset=UTF-8");
									response.setCharacterEncoding("UTF-8");
									response.setStatus(200); // 设置返回状态为OK
									response.getWriter().write("{\"errorCode\":\"5001\",\"description\":\"error\"}");
								}
							}
						}
					} else {
						logger.info("文件服务管理：请求uri非法，鉴权失败！");
						response.setContentType("text/html;charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setStatus(200); // 设置返回状态为OK
						response.getWriter().write("{\"errorCode\":\"5004\",\"description\":\"error\"}");
					}
				} else {
					logger.info("文件服务管理：密钥非法，鉴权失败！");
					response.setContentType("text/html;charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
					response.setStatus(200); // 设置返回状态为OK
					response.getWriter().write("{\"errorCode\":\"5004\",\"description\":\"error\"}");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 下载图片
	 * 
	 * @throws ParseException
	 */
	@RequestMapping(value = "/imagedownload/{contentID}/{devID}/{stamptime}/{life}")
	public void downloadImage(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("contentID") String contentID, @PathVariable("devID") String devID,
			@PathVariable("stamptime") String stamptime, @PathVariable("life") String life)
			throws ServletException, IOException {
		String apiip = IpTools.getIpAddr(request);
		logger.info("进入上传下载业务，访问ip：" + apiip);
		// 对地址进行解码
		String type = contentID.substring(contentID.lastIndexOf("_") + 1).toLowerCase();
		String name = contentID.substring(0, contentID.lastIndexOf("_"));
		// 默认保存文件名
		String downloadName = name + "." + type;
		// 文件路径
		uploadFilePath = PropertyUtil.getValue("path") + "/" + devID + "/" + downloadName;
		// 如果服务器已经存在和上传文件同名的文件，则输出提示信息
		File tempFile = new File(uploadFilePath);
		// 判断文件是否存在
		if (!tempFile.exists()) {
			logger.info("文件不存在~");
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(200); // 设置返回状态为OK
			response.getWriter().write("{\"errorCode\":\"5004\",\"description\":\"error\"}");
		} else {
			// 文件的存放路径
			InputStream inStream = new FileInputStream(uploadFilePath);
			// 设置输出的格式
			response.reset();
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment; name=\"" + downloadName + "\"; filename=\"" + downloadName);
			// 循环取出流中的数据
			byte[] b = new byte[100];
			int len;
			try {
				while ((len = inStream.read(b)) > 0)
					response.getOutputStream().write(b, 0, len);
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除图片
	 */
	@RequestMapping(method = RequestMethod.POST, value = "deteleImage")
	public void deleteImageByContentID(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String sourceIp = request.getRemoteAddr();
		String[] headerEntries = DigestAuthUtils.splitIgnoringQuotes(request.getHeader("HOA_auth"), ',');
		Map<String, String> headerMap = DigestAuthUtils.splitEachArrayElementAndCreateMap(headerEntries, "=", "\"");
		String key = headerMap.get("key");
		if (digestAuth.digestAuth(sourceIp,key)) {
			String devID = request.getParameter("devID");
			String contentID = request.getParameter("contentID");
			// 对contentID进行处理加工，生成图片名
			String name = contentID.substring(0, contentID.lastIndexOf("_"));
			String type = contentID.substring(contentID.lastIndexOf("_") + 1).toLowerCase();
			String filename = name + "." + type;
			uploadFilePath = PropertyUtil.getValue("path") + devID + "/";
			// 如果服务器存在文件，则执行删除
			File tempFile = new File(uploadFilePath + filename);
			if (tempFile.exists()) {
				boolean delResult = tempFile.delete();
				logger.info("是否删除文件：" + delResult);
				logger.info("文件服务管理：文件存在，模拟删除！");
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(200); // 设置返回状态为OK
				response.getWriter().write("{\"errorCode\":\"0\",\"description\":\"success\"}");
			} else {
				logger.info("文件服务管理：文件不存在，返回成功！");
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setStatus(200); // 设置返回状态为OK
				response.getWriter().write("{\"errorCode\":\"0\",\"description\":\"success\"}");
			}
		} else {
			response.getWriter().write("{\"errorCode\":\"1\",\"description\":\"error\"}");
			logger.info("文件服务管理：自服务内部鉴权未通过！");
		}
	}

}