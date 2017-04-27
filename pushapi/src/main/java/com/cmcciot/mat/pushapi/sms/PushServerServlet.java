package com.cmcciot.mat.pushapi.sms;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cmcciot.mat.pushapi.sms.util.Util;

public class PushServerServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (request.getQueryString() == null || request.getQueryString().equals(""))
		{
			PrintWriter writer = response.getWriter();
			writer.write("Oops, Access smsapi OK, but refused!");
			writer.flush();
			writer.close();
			return;
		}
		// doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{

			long beginTime = System.currentTimeMillis();
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(200); // 设置返回状态为OK
			request.setCharacterEncoding("UTF-8");

			RouteService router = RouteService.getRouteService();
			router.doProcess(request, response);
			long finishTime = System.currentTimeMillis();
			Util.log.debug("sms server response duration: [" + (finishTime - beginTime) + "]");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
