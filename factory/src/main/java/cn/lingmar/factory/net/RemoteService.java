package cn.lingmar.factory.net;

import cn.lingmar.factory.model.api.RspModel;
import cn.lingmar.factory.model.api.account.AccountRspModel;
import cn.lingmar.factory.model.api.account.RegisterModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 网络请求的所有接口
 */
public interface RemoteService {

    /**
     * 网络请求的一个注册接口
     *
     * @param model 传入的是RegisterModel
     * @return 返回的是RspModel<AccountRspModel>
     */
    @POST
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);
}
