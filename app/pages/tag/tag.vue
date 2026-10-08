<template>
	<view class="index">
		<view class="tags">

			<block v-for="(value, index) in data" :key="index">
				<view class="tag" @tap="goList(value)">
					<image class="tag-img" :src="value.icon"></image>
					<text class="tag-text">{{value.name}}</text>
				</view>
			</block>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				data: [],
				imgList: [
					"../../static/upload.png"
				],
				//文件实体地址，搜索完后根据该地址进行删除
				filePath: '',
				//搜索的图片指纹
				fingerprint: ''
			}
		},
		onShow: function(options) {
			var that = this
			uni.showLoading({
				title: "页面刷新中"
			});
			setTimeout(function() {
				//执行请求获取分类信息
				uni.request({
					url: that.$serverUrl + 'tagList',
					method: 'GET',
					header: {
						//token
						'token': uni.getStorageSync('token')
					},
					success: (ret) => {
						that.data = ret.data.data
					}
				});
				uni.hideLoading();
			}, 500);
		},
		methods: {
			goList(value) {
				uni.navigateTo({
					url: '../list/list?title=' + value.name + '&id=' + value.id
				})
			},
			//以图识图
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
								console.log(that.avatarUrl)
								console.log(that.userImageUrl)
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
													//组装参数跳转到搜索结果界面
													uni.navigateTo({
														url: '../search/list?data=' + JSON.stringify(res.data.data)
													})
												} else {
													uni.showLoading({
														title: "没有相似的图片"
													});
													setTimeout(function() {
														uni.hideLoading()
													}, 500);
												}
											}
										}
									});
								}
							}
						})
					}

				})

			},
		}
	}
</script>

<style>

</style>
