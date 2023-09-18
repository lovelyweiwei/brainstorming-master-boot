import { userLogoutUsingPOST } from '@/services/brarinstorming/userController';
import { LogoutOutlined } from '@ant-design/icons';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { history, useModel } from '@umijs/max';
import { Col, Modal, Row, Spin, Typography } from 'antd';
import { stringify } from 'querystring';
import type { MenuInfo } from 'rc-menu/lib/interface';
import React, { useCallback, useState } from 'react';
import { flushSync } from 'react-dom';
import HeaderDropdown from '../HeaderDropdown';

export type GlobalHeaderRightProps = {
  menu?: boolean;
  children?: React.ReactNode;
};

export const AvatarName = () => {
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState || {};
  return <span className="anticon">{currentUser?.userAccount ? currentUser?.userAccount : '游客'}</span>;
};

export const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({ children }) => {
  /**
   * 退出登录，并且将当前的 url 保存
   */
  const loginOut = async () => {
    await userLogoutUsingPOST();
    const { search, pathname } = window.location;
    const urlParams = new URL(window.location.href).searchParams;
    /** 此方法会跳转到 redirect 参数所在的位置 */
    const redirect = urlParams.get('redirect');
    // Note: There may be security issues, please note
    if (window.location.pathname !== '/user/login' && !redirect) {
      history.replace({
        pathname: '/user/login',
        // pathname: '/questionset/all',
        search: stringify({
          redirect: pathname + search,
        }),
      });
    }
  };

  /**
   * 跳转注册
   */
  const register = async () => {
    history.replace({
      pathname: '/user/register',
    });
  };

  const actionClassName = useEmotionCss(({ token }) => {
    return {
      display: 'flex',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      alignItems: 'center',
      padding: '0 8px',
      cursor: 'pointer',
      borderRadius: token.borderRadius,
      '&:hover': {
        backgroundColor: token.colorBgTextHover,
      },
    };
  });
  const { initialState, setInitialState } = useModel('@@initialState');
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const { Paragraph } = Typography;

  const onMenuClick = useCallback(
    (event: MenuInfo) => {
      const { key } = event;
      switch (key) {
        case 'logout':
          flushSync(() => {
            setInitialState((s) => ({ ...s, currentUser: undefined }));
          });
          loginOut();
          break;
        // case 'AK/SK':
        //   setModalVisible(true);
        //   break;
      }
    },
    [setInitialState],
  );

  const loading = (
    <span className={actionClassName}>
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    </span>
  );

  if (!initialState) {
    return loading;
  }

  const { currentUser } = initialState;

  // if (!currentUser || !currentUser.userName) {
  //   return loading;
  // }

  const menuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '注销',
    },
  ];
  // const menuItems = currentUser
  //   ? [
  //     {
  //       key: 'logout',
  //       icon: <LogoutOutlined />,
  //       label: '注销',
  //     },
  //   ]
  //   : [
  //     {
  //       key: 'login',
  //       icon: <UserOutlined />,
  //       label: '登录',
  //     },
  //     {
  //       key: 'register',
  //       icon: <SettingOutlined />,
  //       label: '注册',
  //     },
  //   ];
  /*
  * [
        {
          key: "AK/SK",
          icon: <KeyOutlined/>,
          label: 'API密钥'
        },
        {
          key: 'logout',
          icon: <LogoutOutlined />,
          label: '退出',
        }
      ] :
  * */

  return (
    <>
      <HeaderDropdown
        menu={{
          selectedKeys: [],
          onClick: onMenuClick,
          items: menuItems,
        }}
      >
        {children}
      </HeaderDropdown>

      <Modal open={modalVisible} onCancel={() => setModalVisible(false)} footer={null}>
        <Row style={{ padding: '6px 0' }}>
          <Col style={{ color: 'rgba(0,0,0,.4)' }} flex="80px">
            AccessKey
          </Col>
          <Col flex="auto">
            <Paragraph style={{ color: 'rgba(0,0,0,.9)' }} copyable>
              {/*{currentUser.accessKey}*/}
            </Paragraph>
          </Col>
        </Row>

        <Row style={{ padding: '6px 0' }}>
          <Col style={{ color: 'rgba(0,0,0,.4)' }} flex="80px">
            SecretKey
          </Col>
          <Col flex="auto">
            <Paragraph style={{ color: 'rgba(0,0,0,.9)' }} copyable>
              {/*{currentUser.secretKey}*/}
            </Paragraph>
          </Col>
        </Row>
      </Modal>
    </>
  );
};
