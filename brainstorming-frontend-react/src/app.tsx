import { AvatarDropdown, AvatarName } from '@/components/AvatarDropdown';
import { getLoginUserUsingGET } from '@/services/brarinstorming/userController';
import { SettingDrawer } from '@ant-design/pro-layout';
import type { RunTimeLayoutConfig } from '@umijs/max';
import { history } from '@umijs/max';
import { message } from 'antd';
import defaultSettings from '../config/defaultSettings';
import {requestConfig} from "@/requestConfig";

const loginPath = '/user/login';

/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
export async function getInitialState(): Promise<{
  currentUser?: API.LoginUserVO;
}> {
  const fetchUserInfo = async () => {
    try {
      const res = await getLoginUserUsingGET();
      return res.data;
    } catch (error) {
      history.push(loginPath);
    }
    return undefined;
  };
  // 如果不是登录页面，执行
  const { location } = history;
  if (location.pathname !== loginPath) {
    const currentUser = await fetchUserInfo();
    return {
      currentUser,
    };
  }
  return {};
}

// const loginPath = 'http://localhost:8121/oj/user/login';
//
// /**
//  * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
//  * */
// export async function getInitialState(): Promise<{
//   settings?: Partial<LayoutSettings>;
//   currentUser?: User.UserInfo;
//   loading?: boolean;
//   fetchUserInfo?: () => Promise<User.UserInfo | undefined>;
// }> {
//   //获取用户信息，这是一个异步请求(只是一个方法，不会真正执行)
//   const fetchUserInfo = async () => {
//     try {
//       const res = await getLoginUserUsingGET();
//       return res.data;
//     } catch (error) {
//       message.error('未登录！');
//       history.push(`${loginPath}?redirect=http://localhost:8121/oj${history.location.pathname}`);
//     }
//     return undefined;
//   };
//
//   const currentUser = await fetchUserInfo();
//   return {
//     fetchUserInfo,
//     currentUser,
//     settings: defaultSettings as Partial<LayoutSettings>,
//   };
// }

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({ initialState, setInitialState }) => {
  return {
    avatarProps: {
      src: initialState?.currentUser?.userAvatar,
      title: <AvatarName />,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    onPageChange: () => {
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        message.error('未登录！');
        history.push(`${loginPath}`);
      }
    },
    layoutBgImgList: [
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    menuHeaderRender: undefined,
    // 自定义 403 页面
    unAccessible: <div>无权限</div>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      return (
        <>
          {children}
          <SettingDrawer
            disableUrlParams
            enableDarkTheme
            settings={defaultSettings}
            onSettingChange={(settings) => {
              setInitialState((preInitialState) => ({
                ...preInitialState,
                settings,
              }));
            }}
          />
        </>
      );
    },
    ...initialState?.settings,
  };
};

export const request = {
  ...requestConfig,
};
