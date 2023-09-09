/**
 * 检查权限（判断当前用户是否拥有某个权限）
 */
import ACCESS_ENUM from "@/access/accessEnum";

const checkAccess = (loginUser: any, needAccess = ACCESS_ENUM.NOT_LOGIN) => {
  //获取当前登录用户具有哪些权限(如果没有，则表示未登录)
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true;
  }
  // 如果需要用户登录才能访问
  if (needAccess === ACCESS_ENUM.USER) {
    // 如果未登录，无权限
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false;
    }
  }
  // 如果需要管理员登录才能访问
  if (needAccess === ACCESS_ENUM.ADMIN) {
    // 如果不是管理员，无权限
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false;
    }
  }
  return true;
};

export default checkAccess;
