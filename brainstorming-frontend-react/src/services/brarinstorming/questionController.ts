// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** doQuestionRun POST /oj/question */
export async function doQuestionRunUsingPOST(
  body: API.QuestionRunRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseQuestionRunResult_>('/oj/question', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** addQuestion POST /oj/question/add */
export async function addQuestionUsingPOST(
  body: API.QuestionAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/oj/question/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteQuestion POST /oj/question/delete */
export async function deleteQuestionUsingPOST(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/oj/question/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** editQuestion POST /oj/question/edit */
export async function editQuestionUsingPOST(
  body: API.QuestionEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/oj/question/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getQuestionById GET /oj/question/get */
export async function getQuestionByIdUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseQuestionVO_>('/oj/question/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getQuestionVOById GET /oj/question/get/vo */
export async function getQuestionVOByIdUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionVOByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseSafeQuestionVO_>('/oj/question/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listQuestionByPage POST /oj/question/list/page */
export async function listQuestionByPageUsingPOST(
  body: API.QuestionQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageQuestion_>('/oj/question/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listQuestionVOByPage POST /oj/question/list/page/vo */
export async function listQuestionVOByPageUsingPOST(
  body: API.QuestionQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageQuestionVO_>('/oj/question/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listMyQuestionVOByPage POST /oj/question/my/list/page/vo */
export async function listMyQuestionVOByPageUsingPOST(
  body: API.QuestionQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageQuestionVO_>('/oj/question/my/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listSafeQuestionVOByPage POST /oj/question/page/vo/safe */
export async function listSafeQuestionVOByPageUsingPOST(
  body: API.QuestionQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageSafeQuestionVO_>('/oj/question/page/vo/safe', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** doQuestionSubmit POST /oj/question/question_submit/do */
export async function doQuestionSubmitUsingPOST(
  body: API.QuestionSubmitAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/oj/question/question_submit/do', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getQuestionSubmitVoById GET /oj/question/question_submit/get/vo */
export async function getQuestionSubmitVoByIdUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionSubmitVoByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseQuestionSubmitVO_>('/oj/question/question_submit/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listQuestionSubmitByPage POST /oj/question/question_submit/list/page */
export async function listQuestionSubmitByPageUsingPOST(
  body: API.QuestionSubmitQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageQuestionSubmitVO_>('/oj/question/question_submit/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getSubmitSummary GET /oj/question/summary */
export async function getSubmitSummaryUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseSubmitSummaryVO_>('/oj/question/summary', {
    method: 'GET',
    ...(options || {}),
  });
}

/** getQuestionTags GET /oj/question/tags */
export async function getQuestionTagsUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseListString_>('/oj/question/tags', {
    method: 'GET',
    ...(options || {}),
  });
}

/** updateQuestion POST /oj/question/update */
export async function updateQuestionUsingPOST(
  body: API.QuestionUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/oj/question/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
