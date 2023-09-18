import { listQuestionSubmitByPageUsingPOST } from '@/services/brarinstorming/questionController';
import { IconFont } from '@/utils';
import { Color, languageLabel } from '@/utils/constants';
import { useNavigate } from '@@/exports';
import { ProColumns, ProTable } from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { Button, Tooltip } from 'antd';
import moment from 'moment';
import React, { forwardRef, useEffect, useImperativeHandle, useState } from 'react';

type SubmitLogProps = {
  questionId: number;
  ref?: React.ForwardedRef<any>;
};

const SubmitLog: React.FC<SubmitLogProps> = forwardRef(({ questionId }, ref) => {
  const navigate = useNavigate();
  const urlSearchParams = new URLSearchParams(location.search);
  //搜索参数
  const targetSubmitId = Number(urlSearchParams.get('targetSubmitId')) || -1;
  const [current, setCurrent] = useState(() => {
    return Number(urlSearchParams.get('current')) || 1;
  });

  const [dataSource, setDataSource] = useState<API.QuestionSubmitVO[]>([]);
  const [total, setTotal] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);

  //重新获取数据
  const reloadData = () => {
    setLoading(true);
    listQuestionSubmitByPageUsingPOST({
      current: Number(urlSearchParams.get('current')) || 1,
      questionId,
      sortField: 'createTime',
      sortOrder: 'descend',
    })
      .then((res) => {
        if (res.code === 0) {
          if (!res.data.records[0] || res.data.records[0].status !== 1) {
            clearInterval(interval); // 根据特定条件停止轮询
            // console.log('轮询停止');
            setDataSource(res.data.records);
            setTotal(res.data.total);
            setLoading(false);
          } else {
            setDataSource(res.data.records);
            setTotal(res.data.total);
            setLoading(false);
          }
        }
      })

    const interval = setInterval(() => {
      setLoading(true);
      listQuestionSubmitByPageUsingPOST({
        current: Number(urlSearchParams.get('current')) || 1,
        questionId,
        sortField: 'createTime',
        sortOrder: 'descend',
      })
        .then((res) => {
          if (res.code === 0) {
            if (!res.data.records[0] || res.data.records[0].status !== 1) {
              clearInterval(interval); // 根据特定条件停止轮询
              // console.log('轮询停止');
              setDataSource(res.data.records);
              setTotal(res.data.total);
              setLoading(false);
            } else {
              setDataSource(res.data.records);
              setTotal(res.data.total);
              setLoading(false);
            }
          }
        })
        .catch((error) => {
          console.error(error);
          clearInterval(interval); // 请求发生错误时停止轮询
        });
    }, 1200);

    return () => clearInterval(interval); // 清除定时器防止内存泄漏
  };

  // 将 reloadData 方法暴露给父组件
  useImperativeHandle(ref, () => ({
    reloadData,
  }));

  //监听路径参数变化
  useEffect(() => {
    reloadData();
  }, [location.search]);

  //有关搜索参数
  const updateQueryParam = (current: number, targetSubmitId: number) => {
    const params = new URLSearchParams({
      current: current.toString(),
      tab: 'log',
      targetSubmitId: targetSubmitId.toString(),
    });
    //将搜索参数拼接到query上
    navigate({
      search: `?${params.toString()}`,
    });
  };

  const clickInspect = (submitId: number) => {
    updateQueryParam(current, submitId);
  };

  const changePage = (page: number) => {
    //将参数拼接到path上
    setCurrent(page);
    updateQueryParam(page, targetSubmitId);
  };

  const catalog = useEmotionCss(() => {
    return {
      maxHeight: 'calc(100vh - 132px)',
      overflow: 'auto',
      '.title': {
        fontSize: '1.125rem',
        lineHeight: '1.75rem',
      },
    };
  });

  const columns: ProColumns<API.QuestionSubmitVO>[] = [
    {
      title: '状态',
      width: '15%',
      align: 'center',
      render: (_, questionSubmit) => (
        <>
          {(questionSubmit.status === 0 && <span style={{ color: Color.MEDIUM }}>等待中</span>) ||
            (questionSubmit.status === 1 && <span style={{ color: Color.MEDIUM }}>判题中</span>) ||
            (questionSubmit.status === 2 && <span style={{ color: Color.HARD }}>解答错误</span>) ||
            (questionSubmit.status === 3 && <span style={{ color: Color.EASY }}>通过</span>)}
        </>
      ),
    },
    {
      title: '语言',
      width: '15%',
      align: 'center',
      render: (_, questionSubmit) => <>{languageLabel.get(questionSubmit.language)}</>,
    },
    {
      title: '执行用时',
      width: '15%',
      align: 'center',
      render: (_, questionSubmit) => (
        <>{questionSubmit.judgeInfo.time ? `${questionSubmit.judgeInfo.time}ms` : 'N/A'}</>
      ),
    },
    {
      title: '消耗内存',
      width: '15%',
      align: 'center',
      render: (_, questionSubmit) => (
        <>{questionSubmit.judgeInfo.memory ? questionSubmit.judgeInfo.memory : 'N/A'}</>
      ),
    },
    {
      title: '提交人',
      width: '15%',
      align: 'center',
      render: (_, questionSubmit) => (
        <>{questionSubmit.userVO ? questionSubmit.userVO.userName : '无法获取'}</>
      ),
    },
    {
      title: '时间',
      width: '30%',
      align: 'center',
      render: (_, questionSubmit) => {
        return <>{moment(questionSubmit?.createTime).format('YYYY-MM-DD HH:mm:ss')}</>;
      },
    },
    {
      title: '操作',
      valueType: 'option',
      key: 'option',
      width: '10%',
      align: 'center',
      render: (dom, entity) => {
        return (
          <>
            <Tooltip placement="top" title="查看" color="#FA541C">
              <Button
                onClick={() => clickInspect(entity.id)}
                type="text"
                icon={<IconFont type="icon-chakan" />}
              ></Button>
            </Tooltip>
          </>
        );
      },
    },
  ];

  return (
    <div className={catalog}>
      <ProTable<API.QuestionSubmitVO>
        loading={loading}
        dataSource={dataSource}
        columns={columns}
        rowKey="id"
        search={false}
        options={false}
        pagination={{
          total: total,
          current: current,
          pageSize: 10,
          onChange: changePage,
        }}
      />
    </div>
  );
});

export default SubmitLog;
