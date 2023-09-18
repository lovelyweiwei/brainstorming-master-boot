import { IconFont } from '@/utils';
import { DefaultFooter } from '@ant-design/pro-components';
import { history } from '@umijs/max';
import React from 'react';
import {GithubOutlined} from "@ant-design/icons";

const Footer: React.FC = () => {
  // const defaultMessage = '头脑风暴算法平台';

  const whiteList: string[] = ['/questionset/*'];
  const flag = whiteList.some((item) => {
    const regex = new RegExp('^' + item.replace('*', '.*') + '$');
    return regex.test(history.location.pathname);
  });

  if (flag) {
    return null;
  }

  const defaultMessage = 'zw出品';
  const currentYear = new Date().getFullYear();
  const beian = '湘ICP备2023016227号-2';
  const beianUrl = 'https://beian.miit.gov.cn/#/Integrated/index';

  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={
        <>
          {currentYear} {defaultMessage} |{' '}
          <a href={beianUrl} target="_blank" rel="noreferrer">
            {beian}
          </a>
        </>
      }
      links={[
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/lovelyweiwei/brainstorming-master-microservice',
          blankTarget: true,
        },
        {
          key: '头脑风暴算法OJ平台',
          title: <span>头脑风暴算法OJ平台</span>,
          href: 'https://github.com/lovelyweiwei/brainstorming-master-microservice',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
