<template>
  <div class="exampleView">
    <MDEditer :value="codeValue" :handle-change="onCodeChange" />
    <CodeEditor :value="mdValue" :handle-change="onMDChange" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import MDEditer from "@/components/MDEditer.vue";
import CodeEditor from "@/components/CodeEditor.vue"; // @ is an alias to /src

const codeValue = ref();
const mdValue = ref();

const onCodeChange = (v: string) => {
  codeValue.value = v;
  // console.log(v);
};
const onMDChange = (v: string) => {
  mdValue.value = v;
  // console.log(v);
};
</script>
