<template>
  <view>
    <view v-for="(item,index) in list" :key="index">
      <view class="cu-card case">
        <view class="cu-item shadow">
          <view class="image" v-if="type" >
            <image :src="item.files[0]" mode="widthFix" @tap="CviewImage(item.files)">
            </image>
            <view class="cu-tag bg-blue" >{{item.title}}</view>
            <view class="cu-bar bg-shadeBottom" @click="detailInfo(item.id)">
              <text class="text-cut">{{ item.comment }}</text>
            </view>
          </view>
          <view class="image" v-if="!type">
            <image :src="item.photoUrl" mode="widthFix" @tap="viewImage(item.photoUrl)">
            </image>
            <view class="cu-tag bg-blue">{{ item.tagTitle }}</view>
            <view class="cu-bar bg-shadeBottom">
              <text class="text-cut">{{ item.title }}</text>
            </view>
          </view>
          <view class="cu-list menu-avatar">
            <view class="cu-item">
              <view class="cu-avatar round lg" :style=" {backgroundImage:'url('+item.userimageurl+')'}">
              </view>
              <view class="content flex-sub">
                <view class="text-grey">{{ item.userName }}</view>
                <view class="text-gray text-sm flex justify-between">
                  {{ item.dateTime }}
                  <view class="text-gray text-sm">
                    <text class="cuIcon-favor margin-lr-xs" @click="addCollect(item.id)" v-if="!type"></text>
                    {{ item.collectTotal }}
                    <text class="cuIcon-appreciate margin-lr-xs " @click="addLike(item.id)"></text>
                    {{ item.likeTotal }}
                    <text class="cuIcon-message margin-lr-xs" v-if="type"></text>
                    {{ item.commentCount }}
                    <text class="cuIcon-attention margin-lr-xs" v-if="type"></text>
                    {{ item.viewCount }}
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>

</template>

<script>
export default {
  data() {
    return {

    };
  },
  props: {
    list: {
      type: Array,
      default: () => []
    },
    type: {
      type: Boolean
    }
  },
  methods: {
    detailInfo(e){
      uni.navigateTo({
        url:"../photo/commentList?data=" + e
      })
    },
    CviewImage(e){
      uni.previewImage({
        urls: e,
        current: e[0],
        longPressActions: {
          itemList: ["保存图片到本地"],
          success: (data) => {
            if (data.tapIndex == 0) {
              let url = e;
              uni.saveImageToPhotosAlbum({
                filePath: url,
                success: (res) => {
                  uni.showToast({
                    title: "已存至系统相册",
                    icon: "success",
                  });
                },
                fail: (res) => {
                  uni.showToast({
                    title: "保存失败",
                    icon: "error",
                  });
                },
              });
            }
          },
          fail: function (err) {
            console.log(err.errMsg);
          },
        },
      });
    },
    viewImage(e){
      let imgList = []
      imgList.push(e)
      uni.previewImage({
        urls: imgList,
        current: e,
        longPressActions: {
          itemList: ["保存图片到本地"],
          success: (data) => {
            if (data.tapIndex == 0) {
              let url = e;
              uni.saveImageToPhotosAlbum({
                filePath: url,
                success: (res) => {
                  uni.showToast({
                    title: "已存至系统相册",
                    icon: "success",
                  });
                },
                fail: (res) => {
                  uni.showToast({
                    title: "保存失败",
                    icon: "error",
                  });
                },
              });
            }
          },
          fail: function (err) {
            console.log(err.errMsg);
          },
        },
      });
    },
    addCollect(e) {
      var that = this
      uni.showLoading({
        title: "请等待"
      });
      setTimeout(function () {
        uni.request({
          url: that.$serverUrl + 'collect',
          method: 'POST',
          header: {
            'content-type': 'application/json',//自定义请求头信息
            'token':uni.getStorageSync('token')
          },
          data:{
            id:e
          },
          success: (ret) => {
            console.log(ret.data.msg)
            if(ret.data.msg === '请登录'){
              uni.navigateTo({
                url: "../login/login"
              })
            }
            if (ret.data.code !== 200) {
              uni.showToast({
                title: "请求失败",
                icon: "none",
              })
            } else {

              that.$emit('getData');
              uni.showToast({
                title: ret.data.msg,
                icon: "none",
              })
            }
          }
        });
        uni.hideLoading();
      }, 500);
    },
    addLike(e) {
      var that = this
      uni.showLoading({
        title: "请等待"
      });
      setTimeout(function () {
      uni.request({
        url: that.$serverUrl + 'likeTable',
        method: 'POST',
        header: {
          'content-type': 'application/json',//自定义请求头信息
          'token':uni.getStorageSync('token')
        },
        data:{
          id:e,
          type:that.type
        },
        success: (ret) => {
          console.log(ret.data.msg)
          if(ret.data.msg === '请登录'){
            uni.navigateTo({
              url: "../login/login"
            })
          }
          if (ret.data.code !== 200) {
            uni.showToast({
              title: "请求失败",
              icon: "none",
            })
          } else {

            that.$emit('getData');
            uni.showToast({
              title: ret.data.msg,
              icon: "none",
            })
          }
        }
      });
      uni.hideLoading();
    }, 500);
    },
  },
  created: function () {

  }
};
</script>
<style lang="scss">
.tower-swiper .tower-item {
  transform: scale(calc(0.5 + var(--index) / 10));
  margin-left: calc(var(--left) * 100upx - 150upx);
  z-index: var(--index);
}

.container {
  overflow: hidden;
}

.custom-cover {
  flex: 1;
  flex-direction: row;
  position: relative;
}

.cover-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 40px;
  background-color: rgba($color: #000000, $alpha: 0.4);
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-left: 15px;
  font-size: 14px;
  color: #fff;
}

.card-actions {
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  align-items: center;
  height: 45px;
  border-top: 1px #eee solid;
}

.card-actions-item {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.card-actions-item-text {
  font-size: 12px;
  color: #666;
  margin-left: 5px;
}

.cover-image {
  flex: 1;
  height: 150px;
}

.no-border {
  border-width: 0;
}
</style>
