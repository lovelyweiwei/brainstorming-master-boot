export default [
  {
    name: '用户',
    path: '/user',
    layout: false,
    routes: [
      { path: '/user/login', component: './User/Login' },
      { path: '/user/register', name: '注册', component: './User/Register' },
    ],
  },
  {
    path: '/questionset/all',
    name: '题库',
    icon: 'profile',
    component: './Question/QuestionSet',
  },
  {
    path: '/questionset',
    routes: [
      {
        name: '题目详情',
        path: '/questionset/:id',
        component: './Question/QuestionDetail',
        hideInMenu: true,
      },
    ],
  },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    component: './Admin',
  },
  { path: '/', redirect: '/questionset/all' },
  { path: '*', layout: false, component: './404' },
];
