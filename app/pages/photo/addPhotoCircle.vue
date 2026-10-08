<template>
  <form @submit="addPhotoCircle">
    <view class="cu-form-group">
      <view class="title">标题</view>
      <input placeholder="请输入标题" name="title"></input>
    </view>
    <view class="cu-form-group align-start">
      <view class="title">内容</view>
      <textarea maxlength="-1"  @input="textareaBInput" placeholder="请输入内容"></textarea>
    </view>
    <view class="cu-bar bg-white margin-top">
      <view class="action">
        图片上传
      </view>
      <view class="action">
        {{imgList.length}}/4
      </view>
    </view>
    <view class="cu-form-group">
      <view class="grid col-4 grid-square flex-sub">
        <view class="bg-img" v-for="(item,index) in imgList" :key="index" @tap="viewImage"
              :data-url="imgList[index]">
          <image :src="imgList[index]" mode="aspectFill"></image>
          <view class="cu-tag bg-red" @tap.stop="delImg" :data-index="index">
            <text class='cuIcon-close'></text>
          </view>
        </view>
        <view class="solids" @tap="chooseImage" v-if="imgList.length<4">
          <text class='cuIcon-cameraadd'></text>
        </view>
      </view>
    </view>
    <view class="padding flex flex-direction">
      <button form-type="submit" class="cu-btn bg-cyan lg">发布</button>
    </view>
  </form>
</template>

<script>
	export default {
		data() {
			return {
				file: '',
				imgList: [],
        comment:''
			}
		},
		methods: {
      textareaBInput(e) {
        this.comment = e.detail.value
      },
			//发布
			addPhotoCircle(e) {
				//请求注册接口
				uni.request({
					url: this.$serverUrl + 'addPhotoCircle',
					method: 'POST',
					header: {
						'token': uni.getStorageSync('token'),
						'content-type': 'application/json' //自定义请求头信息
					},
					data: {
						'title': e.detail.value.title,
						'comment': this.comment,
						'file': this.imgList.join(",")
					},
					success: (res) => {
						var data = res.data
						uni.showLoading({
							title: data.msg
						});
						setTimeout(function() {
							if (data.code == 200) {
								//跳论坛
								uni.switchTab({
									url: "../photo/photo"
								})
							}
							uni.hideLoading();
						}, 500);
					}
				});
			},
      chooseImage() {
        var that = this
        uni.chooseImage({
          count: 1,
          sizeType: ["compressed"],
          sourceType: ['album', 'camera'], //从相册选择
          success(response) {
            let url = response.tempFilePaths[0];
            uni.uploadFile({
              url: that.$serverUrl + 'uploadImgAddUser',
              filePath: url,
              name: 'file',
              success: (res) => {
                var data = JSON.parse(res.data)
                if (data.code != 200) {
                  uni.showLoading({
                    title: data.msg
                  });
                  setTimeout(function () {
                    uni.hideLoading();
                  }, 500);
                } else {
                  that.imgList.push(data.data.url)
                }
              }
            })
          }
        })
      },
      viewImage(e) {
        uni.previewImage({
          urls: this.imgList,
          current: this.imgList[0]
        });
      },
      delImg(e) {
        uni.showModal({
          title: '提醒',
          content: '是否删除照片？',
          cancelText: '再看看',
          confirmText: '再见',
          success: res => {
            if (res.confirm) {
              this.imgList.splice(e.currentTarget.dataset.index, 1)
            }
          }
        })
      },
		}
	}
</script>

<style>

</style>
