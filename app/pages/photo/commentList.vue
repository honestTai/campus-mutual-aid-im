<template>
  <view>
    <!--主题内容展示-->
    <view class="cu-card dynamic" :class="isCard?'no-card':''">
      <view class="cu-item shadow">
        <view class="cu-list menu-avatar">
          <view class="cu-item">
            <view class="cu-avatar round lg" :style=" {backgroundImage:'url('+data.userimageurl+')'}">
            </view>
            <view class="content flex-sub">
              <view>{{data.userName}}</view>
              <view class="text-gray text-sm flex justify-between">
                {{data.dateTime}}
              </view>
            </view>
          </view>
        </view>
        <view class="text-content">
          主要内容：{{data.comment}}
        </view>

        <!--轮播-->
        <swiper class="screen-swiper" :class="dotStyle?'square-dot':'round-dot'" :indicator-dots="true"
                :circular="true" :autoplay="true" interval="5000" duration="500">
          <swiper-item v-for="(item,index) in data.files" :key="index">
            <image :src="item" mode="aspectFill"></image>
          </swiper-item>
        </swiper>

        <view class="text-gray text-sm text-right padding">
          <text class="cuIcon-appreciate margin-lr-xs" @click="addLike(data.id)"></text>
          {{data.likeTotal}}
          <text class="cuIcon-attention margin-lr-xs"></text>
          {{data.viewCount}}
          <text class="cuIcon-message margin-lr-xs"></text> {{data.commentCount}}
        </view>
        <!--评论-->
        <view class="cu-list menu-avatar comment solids-top" v-for="(comment,index) in data.children"
              :key="index">
          <view class="cu-item">
            <view class="cu-avatar round" :style=" {backgroundImage:'url('+comment.userimageurl+')'}">
            </view>
            <view class="content">
              <view class="text-grey">{{comment.userName}}</view>
              <view class="text-gray text-content text-df">
                {{comment.comment}}
              </view>
              <view class="bg-grey padding-sm radius margin-top-sm  text-sm" v-if="comment.reply!=null">
                <view class="flex">
                  <view>{{userInfo.name}}：</view>
                  <view class="flex-sub">{{comment.reply}}</view>

                </view>
                <view class="flex">
                  <view class="text-white text-df">{{comment.replyTime}}</view>
                </view>
              </view>
              <view class="margin-top-sm flex justify-between">
                <view class="text-gray text-df">{{comment.dateTime}}</view>
                <view>
                  <text class="cuIcon-messagefill text-gray margin-left-sm"
                        v-if="comment.reply==null && userInfo.id==data.userId"
                        @click="replyComment(comment.id)">回复</text>
                  <text class="cuIcon-deletefill text-gray margin-left-sm"
                        v-if="comment.reply==null && userInfo.id==data.userId"
                        @click="delComment(comment.id)">删除</text>
                </view>
              </view>
            </view>
          </view>

        </view>
        <view class="padding-xl bg-white solid-bottom" style="border-radius: 5px;margin: 16px;">
          <form @submit="addComment">
            <view class="cu-form-group">
              <input placeholder="请输入您的评论内容" name="info"></input>
            </view>
            <button form-type="submit" class="cu-btn block line-blue  lg">评论</button>
          </form>
        </view>
      </view>
    </view>

    <view class="cu-modal" :class="modalName=='DialogModal1'?'show':''">
      <view class="cu-dialog">
        <view class="cu-bar bg-white justify-end">
          <view class="content">评论回复</view>
          <view class="action" @tap="hideModal">
            <text class="cuIcon-close text-red"></text>
          </view>
        </view>
        <view class="padding-xl">
          <input placeholder="请输入回复内容" name="info" v-model="reply" @input="onInput"></input>
        </view>
        <view class="cu-bar bg-white justify-end">
          <view class="action">
            <button class="cu-btn line-green text-green" @tap="hideModal">取消</button>
            <button class="cu-btn bg-green margin-left" @tap="replyConfirm">确定</button>

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
				item: {},
				data: {},
        id:null,
        userInfo:uni.getStorageSync('userInfo'),
        modalName:null,
        reply:'',
        commentId:null
			}
		},
		onLoad: function(e) {
			var that = this
      that.id = JSON.parse(e.data)
			uni.showLoading({
				title: "请等待"
			});
			setTimeout(function() {
				that.getData();
				uni.hideLoading();
			}, 500);
		},
		methods: {
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
              type:true
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

                that.getData()
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
      replyConfirm(e) {
        let that = this
        uni.request({
          url: that.$serverUrl + 'replyComment',
          method: 'POST',
          header: {
            'token': uni.getStorageSync('token'),
            'content-type': 'application/json' //自定义请求头信息
          },
          data: {
            'photoCircleId': this.id,
            'id':this.commentId,
            'reply':this.reply
          },
          success: (res) => {
            if(res.data.msg === '请登录'){
              uni.navigateTo({
                url: "../login/login"
              })
            }
            var data=res.data
            uni.showLoading({
              title: data.msg
            });
            setTimeout(function() {
              if(data.code==200){
                that.modalName = null
                that.getData()
              }
              uni.hideLoading();
            }, 500);
          }
        });
      },
      onInput(e) {
        this.reply = e.target.value
      },
      delComment(e){
        var that = this
        uni.request({
          url: that.$serverUrl + 'delMineData/'+e+'/'+4,
          method: 'GET',
          header: {
            'token': uni.getStorageSync('token'),
            'content-type': 'application/json'
          },
          success: (res) => {
            that.getData()
            setTimeout(function () {
              uni.hideLoading();
            }, 500);
          }
        });
      },
      addComment(e){
        let that = this
        uni.request({
          url: that.$serverUrl + 'addComment',
          method: 'POST',
          header: {
            'token': uni.getStorageSync('token'),
            'content-type': 'application/json' //自定义请求头信息
          },
          data: {
            'photoCircleId': this.id,
            'comment':e.detail.value.info
          },
          success: (res) => {
            if(res.data.msg === '请登录'){
              uni.navigateTo({
                url: "../login/login"
              })
            }
            var data=res.data
            uni.showLoading({
              title: data.msg
            });
            setTimeout(function() {
              if(data.code==200){
                that.getData()
              }
              uni.hideLoading();
            }, 500);
          }
        });
      },
      replyComment(e) {
        console.log(e)
        this.commentId = e
        this.modalName = 'DialogModal1'
      },
      hideModal(e) {
        this.modalName = null
      },

			getData() {
				uni.request({
					url: this.$serverUrl + 'detailInfo/'+this.id,
					method: 'GET',
					header: {
						'content-type': 'application/json' //自定义请求头信息
					},
					success: (ret) => {
						console.log("data", ret);
						if (ret.statusCode !== 200) {
							console.log("失败!");
						} else {
							var data = ret.data.data
							this.data=data
						}
					}
				});
			},
		}
	}
</script>

<style>
	.container {
		padding: 20px;
		font-size: 14px;
		line-height: 24px;
	}
</style>
