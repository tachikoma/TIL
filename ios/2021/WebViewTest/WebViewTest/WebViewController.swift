//
//  WebViewController.swift
//  WebViewTest
//
//  Created by Durk-jae Yun on 2021/02/11.
//

import UIKit
import WebKit

final class WKCookieProcessPool: WKProcessPool {
    static let pool = WKCookieProcessPool()
}

class CookieShareWKWebView: WKWebView {
    override init(frame: CGRect, configuration: WKWebViewConfiguration) {
        configuration.processPool = WKCookieProcessPool.pool // cookie 공유
        super.init(frame: frame, configuration: configuration)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

class WebViewController: UIViewController, WKUIDelegate, WKNavigationDelegate {
    var webView: CookieShareWKWebView!

    override func loadView() {
        let webConfiguration = WKWebViewConfiguration()
        webView = CookieShareWKWebView(frame: .zero, configuration: webConfiguration)
        webView.uiDelegate = self
        webView.navigationDelegate = self
        view = webView
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        let myURL = URL(string:"https://m.daum.net")
        let myRequest = URLRequest(url: myURL!)
        webView.load(myRequest)
    }

    func webView(_ webView: WKWebView, didReceive challenge: URLAuthenticationChallenge,
                 completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
        let credential = URLCredential(user: "account",
                                       password: "password",
                                       persistence: .forSession)
        completionHandler(.useCredential, credential)
    }

    /*
     // MARK: - Navigation

     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
         // Get the new view controller using segue.destination.
         // Pass the selected object to the new view controller.
     }
     */
}
