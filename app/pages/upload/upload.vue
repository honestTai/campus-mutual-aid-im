<template>
	<form class='loginView' @submit="upload">
<!--		<view class="cu-form-group margin-top">-->
<!--			<view class="label-view">-->
<!--				<text class="label">分类</text>-->
<!--			</view>-->
<!--			<view class="uni-list-cell-db">-->
<!--				<picker @change="bindPickerChange" :value="index" :range="tagNameList">-->
<!--					<view class="uni-input">{{tagNameList[index]}}</view>-->
<!--				</picker>-->
<!--			</view>-->
<!--		</view>-->
    <view class="cu-form-group margin-top">
      <view class="title">分类</view>
      <picker @change="bindPickerChange" :value="index" :range="tagNameList">
        <view class="picker">
          {{tagNameList[index]}}
        </view>
      </picker>
    </view>
		<!--图片标题-->
    <view class="cu-form-group">
      <view class="title">标题</view>
      <input placeholder="请输入您的标题" name="title"></input>
    </view>
    <view class="cu-bar bg-white margin-top">
      <view class="action">
        图片
      </view>
      <view class="action">
        {{ imgList.length }}/1
      </view>
    </view>
    <view class="cu-form-group">
      <view class="grid col-4 grid-square flex-sub">
        <view class="bg-img" v-for="(item,index) in imgList" :key="index"
              :data-url="imgList[index]">
          <image :src="imgList[index]" mode="aspectFill"></image>
          <view class="cu-tag bg-red" @tap.stop="changeAvatar" :data-index="index">
            <text class='cuIcon-close'></text>
          </view>
        </view>
        <view class="solids" @tap="changeAvatar" v-if="imgList.length<1">
          <text class='cuIcon-cameraadd'></text>
        </view>
      </view>
    </view>
    <view class="padding flex flex-direction">
      <button form-type="submit" class="cu-btn bg-cyan lg">确认</button>
    </view>
	</form>
</template>

<script>
	export default {
		data() {
			return {
				imgList: [

				],
				//图片指纹字符串
				fingerprint: '',
				/**
				 * 图片静态地址
				 */
				photoUrl: '',
				//实体路径
				filePath: '',
				//分类信息
				tagList: [],
				//分类id,默认为1
				tagId: 1,
				//picker显示数据
				tagNameList: [],
				//脚标
				index: 0,
				//图片标题
				title:''
			}
		},
		//初始化执行
		created() {
			this.getTagList()
		},
		methods: {
			//上传图片到服务器
			upload(e) {
				var that = this
				//请求修改个人信息接口
				uni.request({
					url: that.$serverUrl + 'upload',
					method: 'POST',
					header: {
						//token
						'token': uni.getStorageSync('token'),
						'content-type': 'application/json' //自定义请求头信息
					},
					data: {
						//只需传入图片信息与分类id
						'photoUrl': that.photoUrl,
						'fingerprint': that.fingerprint,
						'filePath': that.filePath,
						'tagId': that.tagId,
						'title':e.detail.value.title
					},
					success: (res) => {
						var data = res.data
						uni.showLoading({
							title: data.msg
						});
						setTimeout(function() {
							if (data.code == 200) {
								//清空所有信息
								that.clear()
							} else if (data.msg == '请登录') {
								uni.navigateTo({
									url: "../login/login"
								})
							}
							uni.hideLoading();
						}, 500);
					}
				});
			},
			//上传图片
			changeAvatar(e) {
				var that = this
				uni.chooseImage({
					count: 1,
					sizeType: ["compressed"],
					sourceType: ['album', 'camera'], //从相册选择
					success(response) {
						let url = response.tempFilePaths[0];
						uni.uploadFile({
							url: that.$serverUrl + 'uploadImg',
							filePath: url,
							name: 'file',
							header: {
								//token
								'token': uni.getStorageSync('token')
							},
							success: (res) => {
								console.log(that.avatarUrl)
								console.log(that.userImageUrl)
								var data = JSON.parse(res.data)
								if (data.code != 200) {
									uni.showLoading({
										title: data.msg
									});
									setTimeout(function() {
										if (data.msg == '请登录') {
											uni.navigateTo({
												url: "../login/login"
											})
										}
										uni.hideLoading();
									}, 500);
								} else {
									that.photoUrl = data.data.url
									that.filePath = data.data.filePath
									that.fingerprint = data.data.fingerprint
                  that.imgList.push( data.data.url)
								}
							}
						})
					}

				})

			},
			//清空所有参数
			clear() {
				var that = this
				that.photoUrl = ''
				that.fingerprint = ''
				that.filePath = ''
				that.tagId = 1
				that.index=0
				that.imgList = [

				]
				that.title=''
			},
			//获取所有分类
			getTagList() {
				var that = this
				uni.request({
					url: that.$serverUrl + 'tagList',
					method: 'GET',
					success: (res) => {
						var data = res.data
						uni.showLoading({
							title: data.msg
						});
						setTimeout(function() {
							if (data.code == 200) {
								//处理数据，构造两个一个只显示名称的数组
								that.tagList = data.data
								//深拷贝一下
								let tagListDrop = that.tagList
								tagListDrop.forEach(function(item) {
									that.tagNameList.push(item.name)
								})
							}
							uni.hideLoading();
						}, 500);
						console.log(data.data)
					}
				});
			},
			//改变picker
			bindPickerChange(e) {
				var that = this
				console.log(e)
				that.index = e.target.value
				//通过脚标找到对应的对象，取出id进行绑定
				that.tagId = that.tagList[that.index].id
			},
		}
	}
</script>

<style>

</style>
