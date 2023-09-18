const setingFiance: React.FC<any> = (props) => {
  let timer: any = 0;
  let cont: any = 0;

  const initDate = (name: any) => {
    // 在这里请求接口处理逻辑
    // 根据条件判断 如果接口有数据不需要轮询销毁定时器clearTimerFun()，如果没有数据，创建定时器setTimerFun();
  };

  const clearTimerFun = () => {
    clearInterval(timer);
  };

  const setTimerFun = () => {
    // cont定义了一个全局变量，通过控制轮询的次数来控制相对的定时时间，用来截断定时器
    cont = cont + 1;
    if (cont <= 80) {
      timer = setInterval(() => {
        initDate(accountName);
      }, 10000);
    } else {
      clearTimerFun();
    }
  };
};
export default setingFiance;
