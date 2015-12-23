# DOkHttp
项目中使用的OkHttp 封装使用回调更新UI     上传下载进度更新  


![GIF.gif](https://github.com/Daemon1993/DOkHttp/blob/master/GIF.gif)

使用方法如下

Get  请求

     Request request=new Request.Builder()
                .get()
                .tag(this)
                .url("http:www.baidu.com")
                .build();

        DOkHttp.getInstance().getData4Server(request, new OkHttpUtil.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(String json) {
                changeUI();
            }
        });


Post 请求

    RequestBody requestBody=new FormEncodingBuilder()
                .add("key","value")
                .addHeader(COOKIE, sessionId)  //cookie添加
                .build();

        Request request=new Request.Builder()
                .post(requestBody)
                .tag(this)
                .url("http:www.baidu.com")
                .build();

        DOkHttp.getInstance().getData4Server(request, new OkHttpUtil.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(String json) {
                changeUI();
            }
        });

download progress监听

        Request request=new Request.Builder()
                .tag(this)
                .get()
                .url("http://7xnbj0.com1.z0.glb.clouddn.com/IMG_1919.jpg")
                .build();

        DOkHttp.getInstance().download4ServerListener(request, new DOkHttp.MyCallBack_Progress() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) {
                file=FileUtils.saveFile2Local(response,dir,"download.jpg");
                progressDialog.dismiss();
            }
        }, new DOkHttp.UIchangeListener() {
            @Override
            public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
                int progress= (int) (bytesWrite*100/contentLength);
                Log.e("Download",progress+"");

                progressDialog.setProgress(progress);

                tv_show.append(progress+"\n");
            }
        });


upload  progress监听

        MultipartBuilder mb = new MultipartBuilder();
        mb.type(MultipartBuilder.FORM);
        mb.addFormDataPart("user_id", "74");
        mb.addFormDataPart("user_head", file.getName(), RequestBody.create(null, file));
        RequestBody requestBody = mb.build();

        String url="接口地址";

        DOkHttp.getInstance().uploadPost2ServerProgress(this,url,requestBody,new DOkHttp.MyCallBack(){

            @Override
            public void onFailure(Request request, IOException e) {
                tv_show.setText(e.getMessage());
            }

            @Override
            public void onResponse(String json) {
                progressDialog.dismiss();
                tv_show.setText(json);
            }
        },new DOkHttp.UIchangeListener(){

            @Override
            public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
                int progress= (int) (bytesWrite*100/contentLength);

                progressDialog.setProgress(progress);

                tv_show.append(progress+"\n");
            }
        });

