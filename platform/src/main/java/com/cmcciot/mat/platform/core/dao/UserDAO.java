package com.cmcciot.mat.platform.core.dao;

import java.util.List;

import com.cmcciot.mat.platform.core.bean.UserBean;

public interface UserDAO {

	/**
	 * 新增--请调用Service层添加用户
	 * 
	 * @param userBean
	 * @return
	 */
	String addUser(UserBean userBean);

	/**
	 * 修改
	 * 
	 * @param userBean
	 * @return
	 */
	int updateUser(UserBean userBean);

	/**
	 * 查询
	 * 
	 * @return
	 */
	List<UserBean> queryUser(UserBean userBean);

	/**
	 * 删除
	 * 
	 * @param userId
	 */
	int deleteUser(String userId);

	/**
	 * 查询单个用户
	 * 
	 * @param userId
	 * @return
	 */
	UserBean findUserById(String userId);

	/**
	 * 根据条件查询用户 <功能详细描述>
	 * 
	 * @param userBean
	 * @return [参数说明]
	 * 
	 * @return UserBean [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	UserBean findUser(UserBean userBean);

	/**
	 * 根据用户手机号码更新用户状态
	 * 
	 * @param phoneNo
	 *            手机号码
	 * @param status
	 *            状态
	 * @return int 更新行数
	 **/
	int updateUserStatusByPhone(String phoneNo, String status);

}