<template>
  <view style="height: 100%">
    <view class="cu-bar search bg-white fixed" style="box-shadow: none">
      <view class="search-form round">
        <text class="cuIcon-search"></text>
        <input type="text" placeholder="请输入图片内容标题" confirm-type="search" @confirm="searchHandle" focus/>
      </view>
    </view>
    <view class="margin-top-bar bg-white">
      <view class="cu-bar bg-white solid-bottom margin-top">
        <view class="action">
          <text class="cuIcon-title text-orange "></text>
          分类排序
        </view>
        <view class="action">
          <button class="cu-btn bg-green shadow left:-10px" @tap="showModal" data-target="gridModal">设置</button>

        </view>
      </view>
      <view class="cu-modal" :class="modalName=='gridModal'?'show':''" @tap="hideModal">
        <view class="cu-dialog" @tap.stop>
          <radio-group class="block" @change="sortChange">
            <view class="cu-list menu text-left">
              <view class="cu-item" v-for="(item,index) in sortList" :key="index">
                <label class="flex justify-between align-center flex-sub">
                  <view class="flex-sub">{{ item.name }}</view>
                  <radio class="round" :value="item.value"></radio>
                </label>
              </view>
            </view>
          </radio-group>
        </view>
      </view>
      <view class="cu-modal" :class="modalName=='photoSearchPhoto'?'show':''" @tap="hideModal">
        <view class="cu-dialog" >

        </view>
      </view>
      <view class="cu-list grid" :class="['col-' + gridCol,gridBorder?'':'no-border']">
        <view class="cu-item" v-for="(item,index) in cuIconList" :key="index" v-if="index<gridCol*2"
              @click="searchType(item.id)">
          <view :class="['cuIcon-' + item.cuIcon,'text-' + item.color]">
            <view class="cu-tag badge" v-if="item.badge !=0">
              <block>{{ item.badge > 99 ? '99+' : item.badge }}</block>
            </view>
          </view>
          <text>{{ item.name }}</text>
        </view>
      </view>
      <photo-card :list="list" :type="false" v-on:getData="refreshData"/>

    </view>
  </view>
</template>
<script>
export default {
  data() {
    return {
      refreshing: false,
      providerList: [],
      list: [],
      fetchPageNum: 1,
      gridCol: 5,
      gridBorder: false,
      cuIconList: [],
      tagId: null,
      title: null,
      modalName: null,
      sort: null,
      sortList: [{name: '时间倒叙', value: 0}, {name: '时间正序', value: 1}, {
        name: '收藏倒叙',
        value: 2
      }, {name: '收藏正叙', value: 3}, {name: '点赞倒叙', value: 4}, {name: '点赞倒叙', value: 5}]
    }
  },
  onLoad() {
    var that = this
    uni.showLoading({
      title: "请等待"
    });
    setTimeout(function () {
      that.getData();
      uni.request({
        url: that.$serverUrl + 'tagList',
        method: 'GET',
        header: {
          'content-type': 'application/json' //自定义请求头信息
        },
        success: (ret) => {
          if (ret.statusCode !== 200) {
            uni.showToast({
              title: "请求失败",
              icon: "none",
            })
          } else {
            that.cuIconList = ret.data.data
          }
        }
      });
      uni.hideLoading();
    }, 500);

  },
  onReachBottom() {
    console.log("滑动到页面底部")
    this.getData();
  },
  onPullDownRefresh() {
    console.log("下拉刷新");
    this.refreshing = true;
    this.getData();
  },
  methods: {
    changeAvatar(e) {
      var that = this
      uni.chooseImage({
        count: 1,
        sizeType: ["compressed"],
        sourceType: ['album', 'camera'], //从相册选择
        success(response) {
          that.imgList = response.tempFilePaths
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
                setTimeout(function() {
                  uni.hideLoading();
                }, 500);
              } else {
                //绑值访问搜索接口
                that.filePath = data.data.filePath
                that.fingerprint = data.data.fingerprint
                uni.request({
                  url: that.$serverUrl + 'like',
                  method: 'POST',
                  header: {
                    //token
                    'token': uni.getStorageSync('token')
                  },
                  data: {
                    'filePath': that.filePath,
                    'fingerprint': that.fingerprint
                  },
                  success: (res) => {
                    if (res.data.code != 200) {
                      uni.showLoading({
                        title: res.data.msg
                      });
                      setTimeout(function() {
                        uni.hideLoading()
                      }, 500);
                    } else {
                      if (res.data.data != "") {
                        uni.showLoading({
                          title: "识别成功"
                        },500);
                        that.list= res.data.data
                      } else {
                        uni.showLoading({
                          title: "没有相似的图片"
                        });
                      }
                      setTimeout(function() {
                        uni.hideLoading()
                      }, 500);
                    }
                  }
                });
              }
            }
          })
        }

      })

    },
    sortChange(e) {
      this.sort = e.detail.value
      this.list = []
      this.fetchPageNum = 1
      this.getData()
      this.modalName = null
    },
    hideModal() {
      this.modalName = null
    },
    showModal(e) {
      this.modalName = e.currentTarget.dataset.target
    },
    searchHandle(e) {
      this.title = e.detail.value
      this.list = []
      this.fetchPageNum = 1
      this.getData()
    },
    searchType(e) {
      this.tagId = e
      this.fetchPageNum = 1
      this.list = []
      this.getData()
    },
    refreshData(){
      this.fetchPageNum = 1
      this.list = []
      this.getData()
    },
    getData() {
      uni.request({
        url: this.$serverUrl + 'list',
        method: 'POST',
        header: {
          'content-type': 'application/json' //自定义请求头信息
        },
        data: {
          'pageSize': 10,
          'pageCurrent': this.fetchPageNum,
          'tagId': this.tagId,
          'title': this.title,
          'sort': this.sort
        },
        success: (ret) => {
          if (ret.statusCode !== 200) {
            uni.showToast({
              title: "请求失败",
              icon: "none",
            })
          } else {
            var data = ret.data.data.list
            if (data.length == 0) {
              uni.showToast({
                title: "暂无数据",
                icon: "none",
              })
              this.refreshing = false;
              uni.stopPullDownRefresh();
              return;
            }
            if (this.refreshing) {
              this.refreshing = false;
              uni.stopPullDownRefresh()
              this.list = data;
              this.fetchPageNum = 2;
            } else {
              this.list = this.list.concat(data);
              this.fetchPageNum += 1;
            }

          }
        }
      });
    },
    goDetail(e) {
      uni.navigateTo({
        url: "../detail/detail?data=" + encodeURIComponent(JSON.stringify(e))
      })
    },
    //收藏图片
    collect(e) {
      uni.request({
        url: this.$serverUrl + 'collect',
        method: 'POST',
        header: {
          'token': uni.getStorageSync('token'),
          'content-type': 'application/json' //自定义请求头信息
        },
        data: {
          'id': e.id
        },
        success: (res) => {
          var data = res.data
          uni.showLoading({
            title: data.msg
          });
          setTimeout(function () {
            uni.hideLoading();
          }, 500);
        }
      });
    }
  }
}
</script>

<style>
.list-item {
  background-color: #fff;
}

.wrapper-list {
  white-space: nowrap;
  padding: 0rpx 20rpx 50rpx 0rpx;
}

.wrapper-list .item {
  display: inline-block;
  width: 560rpx;
  height: 800rpx;
  margin: 60rpx 0 60rpx 50rpx;
  padding: 10rpx 30rpx;
  border-radius: 25rpx;
  box-shadow: 0rpx 0rpx 50rpx 10rpx rgba(211, 211, 211, 1);
}

.wrapper-list .item:nth-last-child(1) {
  margin-right: 20rpx;
}

.wrapper-list .item .img-box {
  width: 100%;
  height: 480rpx;
}

.wrapper-list .item .img-box image {
  width: 100%;
  height: 100%;
}

.adsec {
  width: 100%;
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  padding: 7rpx 10rpx;
  height: 80rpx;
}

.adsec-icon {
  height: 80rpx;
  line-height: 80rpx;
}

.swiper_container {
  height: 80rpx;
  width: 95%;
  line-height: 80rpx;
}

.screen-swiper {
  height: 600rpx;
  margin-top: 100rpx;
}

.goods-image {
  width: 100%;
  height: 1150rpx;
}

.hot-goods {
  width: auto;
  overflow: hidden;
}

.goods-buy {
  margin-left: 500rpx;
  margin-top: -120rpx;
  padding-bottom: 50rpx;
}

.buy-now {
  background-color: #2967ff !important;
  font-weight: 300;
  width: 220rpx;
}
</style>
