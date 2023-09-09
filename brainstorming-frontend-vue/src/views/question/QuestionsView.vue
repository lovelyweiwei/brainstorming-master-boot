<template>
  <div id="questionSubmitView">
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="title" label="题目" style="min-width: 320px">
        <a-input v-model="searchParams.title" placeholder="请输入题目名称" />
      </a-form-item>
      <a-form-item field="difficulty" label="难度" style="min-width: 240px">
        <a-select
          v-model="searchParams.difficulty"
          placeholder="请选择难度"
          allow-clear
        >
          <a-option value="">请选择难度</a-option>
          <a-option value="简单">简单</a-option>
          <a-option value="中等">中等</a-option>
          <a-option value="困难">困难</a-option>
        </a-select>
      </a-form-item>
      <a-form-item field="tags" label="标签" style="min-width: 320px">
        <a-input-tag
          v-model="searchParams.tags"
          placeholder="请输入标签(输入完请回车)"
        />
      </a-form-item>
      <a-form-item>
        <a-button status="success" @click="doSearch">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider size="0" />
    <a-table
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="onPageChange"
    >
      <template #difficulty="{ record }">
        <a-space v-if="record.difficulty === '简单'" style="color: forestgreen">
          简单
        </a-space>
        <a-space v-if="record.difficulty === '中等'" style="color: darkorange">
          中等
        </a-space>
        <a-space v-if="record.difficulty === '困难'" style="color: red">
          困难
        </a-space>
      </template>
      <template #tags="{ record }">
        <a-space wrap>
          <a-tag v-for="(tag, index) of record.tags" :key="index" color="green"
            >{{ tag }}
          </a-tag>
        </a-space>
      </template>
      <template #acceptedRate="{ record }">
        {{
          `${
            record.submitNum ? record.acceptedNum / record.submitNum : "0"
          }% (${record.acceptedNum}/${record.submitNum})`
        }}
      </template>
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD") }}
      </template>
      <template #optional="{ record }">
        <a-space>
          <a-button type="primary" @click="toQuestionPage(record)"
            >go!
          </a-button>
        </a-space>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import moment from "moment";

const show = ref(true);

const dataList = ref([]);
const total = ref(0);
const searchParams = ref<QuestionQueryRequest>({
  difficulty: "",
  title: "",
  tags: [],
  pageSize: 10,
  current: 1,
});

const loadData = async () => {
  const res = await QuestionControllerService.listQuestionVoByPageUsingPost(
    searchParams.value
  );
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败，" + res.message);
  }
};

/**
 * 监听 searchParams变化，改变重新加载
 */
watchEffect(() => {
  loadData();
});

/**
 * 页面加载时请求数据
 */
onMounted(() => {
  loadData();
});

const columns = [
  // {
  //   title: "状态",
  //   slotName: "questionStatus",
  // },
  {
    title: "题号",
    dataIndex: "id",
  },
  {
    title: "题目",
    dataIndex: "title",
  },
  {
    title: "难度",
    slotName: "difficulty",
  },
  {
    title: "标签",
    slotName: "tags",
  },
  {
    title: "通过率",
    slotName: "acceptedRate",
  },
  {
    title: "创建时间",
    slotName: "createTime",
  },
  {
    slotName: "optional",
  },
];

const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

const router = useRouter();

/**
 * 跳转到做题页面
 * @param question
 */
const toQuestionPage = (question: Question) => {
  router.push({
    path: `/view/question/${question.id}`,
  });
};

/**
 * 搜索
 */
const doSearch = () => {
  // 搜索把页面置为第一页
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
  // 自动监听了
  // loadData();
};
</script>

<style scoped>
#questionSubmitView {
  max-width: 1280px;
  margin: 0 auto;
}
</style>
