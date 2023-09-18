import Answer from '@/pages/Question/QuestionDetail/compontents/Answer';
import Content from '@/pages/Question/QuestionDetail/compontents/Content';
import Editor from '@/pages/Question/QuestionDetail/compontents/Editor';
import LogDetail from '@/pages/Question/QuestionDetail/compontents/LogDetail';
import SubmitLog from '@/pages/Question/QuestionDetail/compontents/SubmitLog';
import {
  doQuestionRunUsingPOST,
  doQuestionSubmitUsingPOST,
  getQuestionVOByIdUsingGET,
} from '@/services/brarinstorming/questionController';
import { IconFont } from '@/utils';
import { Color } from '@/utils/constants';
import { useModel, useParams } from '@@/exports';
import { history } from '@umijs/max';
import { Button, Card, Col, Divider, message, Row, Skeleton, Space, Tabs, TabsProps } from 'antd';
import TextArea from 'antd/es/input/TextArea';
import 'md-editor-rt/lib/style.css';
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'umi';
import './index.less';

const labelStyle: React.CSSProperties = {
  color: '#3c3c4399',
  fontSize: '.75rem',
  fontWeight: 500,
  marginBottom: 8,
};

const cardStyle: React.CSSProperties = {
  borderRadius: '.5rem',
  backgroundColor: '#000a2008',
  padding: '6px 10px',
};

const QuestionDetail: React.FC = () => {
  const navigate = useNavigate();
  const urlSearchParams = new URLSearchParams(location.search);
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState || {};

  const submitLogRef = useRef(null);

  const [activeTab, setActiveTab] = useState<string>(() => {
    return urlSearchParams.get('tab') || 'content';
  });
  const [targetSubmitId, setTargetSubmitId] = useState<number>(() => {
    return Number(urlSearchParams.get('targetSubmitId')) || -1;
  });

  const [activeTerminal, setActiveTerminal] = useState<string>('1');
  const [coderHeight, setCoderHeight] = useState<string>('100%');
  const [logHeight, setLogHeight] = useState<string>('calc(100vh - 180px)');
  const [terminalOpen, setTerminalOpen] = useState<boolean>(false);

  const [code, setCode] = useState(
    'public class Main {\n\tpublic static void main(String[] args) {\n\t\t\n\t}\n}',
  );

  const [language, setLanguage] = useState('java');

  const params = useParams();
  const questionId = Number(params.id);
  const [question, setQuestion] = useState<API.QuestionVO>();
  const [loading, setLoading] = useState<boolean>(false);
  const [difficultyColor, setDifficultyColor] = useState<string>('green');

  const [resultLoading, setResultLoading] = useState<boolean>(false);

  const [testInput, setTestInput] = useState<string>('');
  const [testResult, setTestResult] = useState<API.QuestionRunResult>();
  const [testResultLoading, setTestResultLoading] = useState<boolean>(false);

  //左侧面板标签页
  const questionItems: TabsProps['items'] = [
    {
      key: 'content',
      label: `题目描述`,
    },
    {
      key: 'answer',
      label: `题解`,
    },
    {
      key: 'log',
      label: `提交记录`,
      disabled: !currentUser,
    },
  ];
  //控制台标签页
  const terminalItems: TabsProps['items'] = [
    {
      key: '1',
      label: `测试用例`,
    },
    {
      key: '2',
      label: `执行结果`,
    },
  ];

  //首先获取题目的全部信息，根据id查询题目信息
  useEffect(() => {
    getQuestionVOByIdUsingGET({ id: questionId }).then((res) => {
      if (res.code === 0) {
        setQuestion(res.data);
        setLoading(false);
        switch (res.data?.difficulty) {
          case '简单':
            setDifficultyColor(Color.EASY);
            break;
          case '中等':
            setDifficultyColor(Color.MEDIUM);
            break;
          case '困难':
            setDifficultyColor(Color.HARD);
            break;
        }
      } else {
        //文章不存在
        history.push('/');
      }
    });
  }, []);

  //监听路径参数变化
  useEffect(() => {
    setActiveTab(urlSearchParams.get('tab') || 'content');
    setTargetSubmitId(Number(urlSearchParams.get('targetSubmitId')) || -1);
  }, [location.search]);

  //更新路径上的query参数
  const updateQuery = (newTab: string, submitId: number) => {
    // console.log(submitId);
    //将搜索参数拼接到query上
    const params = new URLSearchParams({
      tab: newTab,
      targetSubmitId: submitId.toString(),
    });
    navigate({
      search: `?${params.toString()}`,
    });
  };

  //切换tab时将query参数添加到路径上
  const changeTab = (newTab: string) => {
    updateQuery(newTab, targetSubmitId);
  };

  //当获取判题结果后将targetSubmitId设置到路径上
  const changeTargetSubmitId = (submitId: number) => {
    updateQuery(activeTab, submitId);
  };

  //点击运行按钮
  const runQuestion = () => {
    setTestResultLoading(true);
    doQuestionRunUsingPOST({ code, language, input: testInput }).then((res) => {
      if (res.code === 0) {
        message.success('运行成功！');
        setActiveTerminal('2');
        setTestResult(res.data);
        setTestResultLoading(false);
      }
    });
  };

  //点击提交按钮
  const submitQuestion = () => {
    if (question) {
      // setResultLoading(true);
      doQuestionSubmitUsingPOST({ code, language, questionId: question.id }).then((res) => {
        // console.log(res);
        //将搜索参数拼接到query上
        changeTargetSubmitId(res.data); // id
        //如果当前在提交记录tab，要刷新
        if (activeTab === 'log' && submitLogRef.current) {
          // @ts-ignore
          submitLogRef.current.reloadData();
        }
      });
    }
  };

  //获取控制台面板内容
  const getTerminalContent = () => {
    if (activeTerminal === '1') {
      return (
        <>
          <div style={labelStyle}>输入</div>
          <TextArea
            value={testInput}
            onChange={(element) => {
              setTestInput(element.target.value);
            }}
          />
        </>
      );
    } else if (activeTerminal === '2') {
      return testResult ? (
        <>
          <div style={labelStyle}>输入</div>
          <div style={cardStyle}>{testResult.input}</div>
          <div style={{ marginTop: 16 }}></div>
          <div style={labelStyle}>输出</div>
          <div style={cardStyle}>{testResult.output}</div>
        </>
      ) : testResultLoading ? (
        <Skeleton paragraph={{ rows: 4 }}></Skeleton>
      ) : (
        <div
          style={{
            color: '#3c3c4399',
            fontWeight: 500,
            justifyContent: 'center',
            height: '80px',
            display: 'flex',
            alignItems: 'center',
          }}
        >
          请先执行代码
        </div>
      );
    }
  };

  //获取左侧标签页内容
  const getTabContent = () => {
    return loading || !question ? (
      <div style={{ padding: '0 20px' }}>
        <Skeleton paragraph={{ rows: 10 }} />
      </div>
    ) : (
      (activeTab === 'content' && (
        <Content question={question} difficultyColor={difficultyColor} />
      )) ||
        (activeTab === 'answer' && <Answer answer={question.answer} />) ||
        (activeTab === 'log' && <SubmitLog ref={submitLogRef} questionId={question.id} />)
    );
  };

  //获取右侧面板内容
  const getRightPanelContent = () => {
    return targetSubmitId > 0 || resultLoading ? (
      <LogDetail
        afterClose={() => setResultLoading(false)}
        logHeight={logHeight}
        targetSubmitId={targetSubmitId}
      />
    ) : (
      <Editor
        coderHeight={coderHeight}
        code={code}
        setCode={setCode}
        language={language}
        setLanguage={setLanguage}
      />
    );
  };

  //点击控制台按钮
  const clickTerminal = () => {
    setTerminalOpen(!terminalOpen);
    if (terminalOpen) {
      setCoderHeight('100%');
      setLogHeight('calc(100vh - 170px)');
    } else {
      setCoderHeight('calc(100vh - 322px)');
      setLogHeight('calc(100vh - 322px)');
    }
  };

  return (
    <Row style={{ width: '100%', margin: '0 auto' }}>
      <Col span={12} style={{ paddingRight: 4 }}>
        <Card bodyStyle={{ padding: 0 }} style={{ height: 'calc(100vh - 73px)', borderRadius: 4 }}>
          <Tabs
            style={{ padding: '0 16px' }}
            activeKey={activeTab}
            items={questionItems}
            onChange={changeTab}
          />
          {getTabContent()}
        </Card>
      </Col>

      <Col span={12} style={{ paddingLeft: 4 }}>
        <div style={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 73px)' }}>
          <Card
            bodyStyle={{ padding: 0, display: 'flex', flexDirection: 'column', height: '100%' }}
            style={{ flexGrow: 1, marginBottom: 8, borderRadius: 4 }}
          >
            {getRightPanelContent()}
          </Card>

          <Card bodyStyle={{ padding: 0 }} style={{ borderRadius: 4 }}>
            {terminalOpen && (
              <div style={{ position: 'relative', height: 150 }}>
                <Tabs
                  style={{ padding: '0 16px' }}
                  activeKey={activeTerminal}
                  items={terminalItems}
                  onChange={setActiveTerminal}
                />
                <div style={{ maxHeight: 100, overflow: 'scroll' }}>
                  <div style={{ margin: '0 20px 10px 20px' }}>{getTerminalContent()}</div>
                </div>

                <Divider style={{ position: 'absolute', bottom: 0, margin: 0 }} />
              </div>
            )}

            <div style={{ padding: 8 }}>
              <Button
                onClick={clickTerminal}
                type="text"
                size="small"
                style={{ width: 90, height: 28 }}
              >
                控制台 {terminalOpen ? <IconFont type="icon-down" /> : <IconFont type="icon-up" />}
              </Button>

              <Space style={{ float: 'right' }}>
                <Button
                  disabled={!question || !currentUser || targetSubmitId > 0}
                  size="small"
                  style={{ width: 66, height: 28 }}
                  onClick={runQuestion}
                >
                  运行
                </Button>
                <Button
                  disabled={!question || !currentUser || targetSubmitId > 0}
                  onClick={submitQuestion}
                  size="small"
                  style={{ width: 66, height: 28 }}
                  type="primary"
                >
                  提交
                </Button>
              </Space>
            </div>
          </Card>
        </div>
      </Col>
    </Row>
  );
};

export default QuestionDetail;
