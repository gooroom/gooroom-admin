<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<link rel="stylesheet" href="/gpms/vendor/bootstrap-4.3.1-dist/css/bootstrap.min.css"/>
<title>GPMS Notice</title>
<style>
html, body {
    height: 100%;
}
div#root {
    height: 100%;
}
.notice-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 15px;
    max-width: initial;
}
table tr:last-child > td {
    border-bottom: 1px solid #dee2e6;
}
.notice-table {
    table-layout: fixed;
}
.notice-card {
    flex: 1;
    overflow: auto;
}
</style>
</head>
<body>
<div id="root"></div>
<script type="text/babel">
const Pagination = ReactBootstrap.Pagination;
const OverlayTrigger = ReactBootstrap.OverlayTrigger;
const Tooltip = ReactBootstrap.Tooltip;
const BrowserRouter = ReactRouterDOM.BrowserRouter;
const Switch = ReactRouterDOM.Switch;
const Route = ReactRouterDOM.Route;

const parseHash = (hash: string): any => {
    const params = {};
    const pairs = hash.substring(1).split(';');
    for (let i = 0; i < pairs.length; i++) {
        const pair = pairs[i].split('=');
        params[decodeURIComponent(pair[0])] = decodeURIComponent(pair[1] || '');
    }
    return params;
};

class NoticeApp extends React.Component {
    constructor() {
        super();
        this.state = {
            notices: [],
            currentNotice: undefined,
            previousPage: 1,
            currentPage: 1,
            size: 5,
            totalItems: 0,
            links: {}
        };
    }

    componentDidMount() {
        const { currentPage } = this.state;

        let currentPagePromise = new Promise((resolve, reject) => resolve({ data: { pageNumber: currentPage } }));
        const noticePublishId = parseHash(location.hash)['npid'];
        if (typeof noticePublishId !== 'undefined' && noticePublishId.length > 0) {
            currentPagePromise = this.searchPageNumber(noticePublishId);
            this.getNotice(noticePublishId);
        }

        currentPagePromise.then(res => this.getNotices(res.data.pageNumber));
    }

    shouldComponentUpdate(nextProps, nextState) {
        if (typeof nextState.currentNotice === 'undefined' && nextState.notices.length > 0) {
            this.getNotice(nextState.notices[0].noticePublishId);
        }
        return true;
    }

    getNotices(page) {
        const { size } = this.state;

        axios.get('/gpms/apis/notices', {
            params: { page: page, size: size, sort: 'openDt,desc' }
        }).then(res => {
            this.setState({
                notices: res.data,
                links: this.parseLinks(res.headers['link']),
                totalItems: res.headers['X-Total-Count'],
                currentPage: page,
                previousPage: page
            });
        });
    }

    getNotice(noticePublishId) {
        axios.get('/gpms/apis/notices/'+ noticePublishId).then(res => {
            this.setState({ currentNotice: res.data }); 
        });
    }

    searchPageNumber(noticePublishId) {
        const { size } = this.state;

        return axios.get('/gpms/apis/notices/'+ noticePublishId + '/pagenumber', {
            params: { size: size, sort: 'openDt,desc' }
        });
    }

    loadPage(page) {
        const { previousPage } = this.state;

        if (page !== previousPage) {
            this.getNotices(page);
        }
    }

    handleClickTitle(event, notice) {
        this.getNotice(notice.noticePublishId);
        notice.openedDt = Date.now();
    }

    parseLinks(header) {
        const parts = header.split(',');
        const links = {};
        parts.forEach(p => {
            const section = p.split(';');
            const url = section[0].replace(/<(.*)>/, '$1').trim();
            const queryString: any = {};
            url.replace(new RegExp('([^?=&]+)(=([^&]*))?', 'g'), ($0, $1, $2, $3) => (queryString[$1] = $3));
            let page: any = queryString.page;
            if (typeof page === 'string') {
                page = parseInt(page, 10);
            }
            const name: string = section[1].replace(/rel="(.*)"/, '$1').trim();
            links[name] = page;
        });
        return links;
    }

    render() {
        const { notices, currentNotice, links, currentPage } = this.state;
        const emptyRows = 5 - notices.length;
        
        let pageItems = [];
        const startPageNumber = typeof links.prev !== 'undefined' ? links.prev + 1 : 1;
        const lastPageNumber = typeof links.next !== 'undefined' ? links.next + 1 : links.last + 1;
        for (let pageNumber = startPageNumber; pageNumber <= lastPageNumber; pageNumber++) {
            pageItems.push(<Pagination.Item onClick={e => this.loadPage(pageNumber)} active={currentPage === pageNumber}>{pageNumber}</Pagination.Item>);
        }

        return (
        <div class="container notice-container">
            <h4>공지 목록</h4>
            <table class="table table-hover table-sm notice-table">
            <tbody>
                {notices.map((notice, i) => {

                let className = '';
                if (typeof currentNotice !== 'undefined' && notice.noticePublishId === currentNotice.noticePublishId) {
                    className = 'table-active';
                } else if (!notice.openedDt) {
                    className = 'font-weight-bold';
                }

                return (
                <tr id={notice.noticePublishId} key={'notice-' + i} ref={notice.noticePublishId} className={className}>
                    <td class="w-50 text-truncate">
                        <OverlayTrigger placement="bottom" overlay={<Tooltip id="tooltip">{notice.title}</Tooltip>}>
                            <a href="#" onClick={e => this.handleClickTitle(e, notice)}>{notice.title}</a>
                        </OverlayTrigger>
                    </td>
                    <td class="w-25 text-truncate">{notice.regUserId}</td>
                    <td class="w-25 text-truncate">{moment(notice.openDt).format('YYYY-MM-DD HH:mm')}</td>
                </tr>
                )})}
                {emptyRows > 0 && Array.from(Array(emptyRows).keys()).map((n) => {
                return (
                <tr id={'notice-e-' + n} key={'notice-e-' + n}>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
                )})}
            </tbody>
            </table>
            <Pagination className="justify-content-center" size="sm">
                <Pagination.First onClick={e => this.loadPage(links.first + 1)} disabled={typeof links.prev === 'undefined'}/>
                <Pagination.Prev onClick={e => this.loadPage(links.prev + 1)} disabled={typeof links.prev === 'undefined'}/>
                {links.last > 1 && (links.next > 2 || typeof links.next === 'undefined') && (<Pagination.Ellipsis disabled/>)}
                {pageItems}
                {links.last > 1 && (links.prev < links.last - 1 || typeof links.prev === 'undefined') && (<Pagination.Ellipsis disabled/>)}
                <Pagination.Next onClick={e => this.loadPage(links.next + 1)} disabled={typeof links.next === 'undefined'}/>
                <Pagination.Last onClick={e => this.loadPage(links.last + 1)} disabled={typeof links.next === 'undefined'}/>
            </Pagination>
            <div class="card notice-card">
                <div class="card-body">
                    {notices.length > 0 && currentNotice &&
                        <pre style={{fontSize: '16px'}}>{currentNotice.content}</pre>
                    }
                </div>
            </div>
        </div>
        );
    }
}
ReactDOM.render((
    <BrowserRouter>
        <Switch>
            <Route path="/" component={() => <NoticeApp/>}/>
        </Switch>
    </BrowserRouter>
), document.getElementById('root'));
</script>
<script crossorigin src="/gpms/vendor/react/16.8.3/umd/react.production.min.js"></script>
<script crossorigin src="/gpms/vendor/react-dom/16.8.3/umd/react-dom.production.min.js"></script>
<script crossorigin src="/gpms/vendor/react-router-dom/4.3.1/umd/react-router-dom.min.js"></script>
<script crossorigin src="/gpms/vendor/react-bootstrap/1.0.0-beta.5/dist/react-bootstrap.js"></script>
<script crossorigin src="/gpms/vendor/axios/0.18.0/dist/axios.min.js"></script>
<script crossorigin src="/gpms/vendor/moment/2.24.0/min/moment.min.js"></script>
<script crossorigin src="/gpms/vendor/babel-core/5.8.34/browser.js"></script>
</body>
</html>


