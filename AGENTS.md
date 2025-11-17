<!-- 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

## 代码和注释
注释请使用中文。

## Commit 格式规范

Commit 分为“标题”和“内容”。type和scope保持使用英文，subject和内容使用中文。

1. 标题

   `[<type>](<scope>) <subject> (#pr)`

    * `<type>`

      本次提交的类型，限定在以下类型（全小写）

        * fix：bug 修复
        * feature：新增功能
        * feature-wip：开发中的功能，比如某功能的部分代码。
        * improvement：原有功能的优化和改进
        * style：代码风格调整
        * typo：代码或文档勘误
        * refactor：代码重构（不涉及功能变动）
        * performance/optimize：性能优化
        * test：单元测试的添加或修复
        * chore：构建工具的修改
        * revert：回滚
        * deps：第三方依赖库的修改
        * community：社区相关的修改，如修改 Github Issue 模板等。

      几点说明：

        1. 如在一次提交中出现多种类型，需增加多个类型。
        2. 如代码重构带来了性能提升，可以同时添加 [refactor][optimize]
        3. 不得出现如上所列类型之外的其他类型。

    * `<scope>`

      本次提交涉及的模块范围。因为功能模块繁多，在此仅罗列部分，后续根据需求不断完善。

        * planner
        * meta
        * storage
        * stream-load
        * broker-load
        * routine-load
        * sync-job
        * export
        * executor
        * spark-connector
        * flink-connector
        * datax
        * log
        * cache
        * config
        * vectorization
        * docs
        * profile

    * `<subject>`

      标题需尽量清晰表明本次提交的主要内容。

2. 内容

   commit message 需遵循以下格式：

    ```
    issue：#7777
    
    your message
    ```

    1. 如无 issue，可不填。issue 也可以出现在 message 里。
    1. 一行原则不超过 100 个字符。

3. 示例

    ```
    [fix](executor) 调整 DateTimeValue 的内存布局以适配加载逻辑（#7022）
    将 DateTimeValue 的内存布局还原为旧版样式，以此解决兼容性问题。
    ```
