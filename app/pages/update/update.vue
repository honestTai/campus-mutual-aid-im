<template>
	<form class='loginView' @submit="update">
		<view class="input-view">
			<view class="label-view">
				<text class="label">姓名 </text>
			</view>
			<input class="input" type="text" placeholder="请输入用户名" name="name" :value="userInfo.name" />
		</view>
		<view class="input-view">
			<view class="label-view">
				<text class="label">账号 </text>
			</view>
			<input class="input" type="text" placeholder="请输入登录账号" name="num" :value="userInfo.num" />
		</view>
		<view class="input-view">
			<view class="label-view">
				<text class="label">密码 </text>
			</view>
			<input class="input" type="password" placeholder="请输入登录密码" name="pwd" :value="userInfo.pwd" />
		</view>
		<view class="input-view">
			<view class="label-view">
				<text class="label">头像</text>
			</view>
			<!-- 图片 -->
			<view @click="changeAvatar">
				<view v-for="(item,index) in imgList" :key="index">
					<image class="logo-img" :src="imgList[index]"></image>
				</view>
			</view>
		</view>
		<view class="button-view">
			<button type="default" class="login" hover-class="hover" formType="submit">确认</button>
		</view>
	</form>
</template>

<script>
	export default {
		data() {
			return {
				userImageUrl: '',
				imgList: [
					"../../static/logo.png"
				],
				userInfo: {},
				id: uni.getStorageSync('userInfo').id
			}
		},
		//初始化
		created() {
			this.getUserInfo()
		},
		methods: {
			//初始化获取个人信息
			getUserInfo(e) {
				var that = this
				//绑值
				that.userInfo = uni.getStorageSync('userInfo')
				if (that.userInfo.userImageUrl != "") {
					that.userImageUrl = that.userInfo.userImageUrl
					that.imgList.splice(0, 1, that.userInfo.userImageUrl)
				}
			},
			//修改个人信息
			update(e) {
				//请求修改个人信息接口
				uni.request({
					url: this.$serverUrl + 'updateUser',
					method: 'POST',
					header: {
						//token
						'token': uni.getStorageSync('token'),
						'content-type': 'application/json' //自定义请求头信息
					},
					data: {
						'num': e.detail.value.num,
						'pwd': e.detail.value.pwd,
						'name': e.detail.value.name,
						'userImageUrl': this.userImageUrl,
						'id': this.id
					},
					success: (res) => {
						var data = res.data
						uni.showLoading({
							title: data.msg
						});
						setTimeout(function() {
							if (data.code == 203) {
								//跳到登录页面，并清除所有缓存
								uni.removeStorageSync("token")
								uni.removeStorageSync("userInfo")
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
						console.log(that.imgList)
						that.imgList = response.tempFilePaths
						console.log(that.imgList)
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
									that.userImageUrl = data.data.url
								}
							}
						})
					}

				})

			}
		}
	}
</script>

<style>

</style>
